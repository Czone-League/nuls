/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.db.manager;

import io.nuls.core.tools.cfg.ConfigLoader;
import io.nuls.core.tools.log.Log;
import io.nuls.core.tools.str.StringUtils;
import io.nuls.kernel.cfg.NulsConfig;
import io.nuls.kernel.constant.KernelErrorCode;
import io.nuls.kernel.model.Result;
import org.iq80.leveldb.*;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;

/**
 * @Desription:
 * @Author: PierreLuo
 * @Date:
 */
public class LevelDBManager {

    private static int max;

    private static final ConcurrentHashMap<String, DB> AREAS = new ConcurrentHashMap<>();

    private static final String BASE_DB_NAME = "leveldb";

    private static volatile boolean isInit = false;

    private static String dataPath;

    public static int getMax() {
        return max;
    }

    public static synchronized void init() throws Exception {
        if (!isInit) {
            isInit = true;
            File dir = loadDataPath();
            dataPath = dir.getPath();
            Log.info("LevelDBManager dataPath is " + dataPath);
            File[] areaFiles = dir.listFiles();
            DB db = null;
            for (File areaFile : areaFiles) {
                if (!areaFile.isDirectory()) {
                    continue;
                }
                try {
                    db = openDB(areaFile.getPath() + File.separator + BASE_DB_NAME, false);
                    AREAS.put(areaFile.getName(), db);
                } catch (Exception e) {
                    Log.warn("load area failed, areaName: " + areaFile.getName(), e);
                }

            }
        }
    }

    private static File loadDataPath() throws Exception {
        Properties properties = ConfigLoader.loadProperties("db_config.properties");
        String path = properties.getProperty("leveldb.datapath", "./data/kv");
        String max_str = properties.getProperty("leveldb.area.max", "20");
        try {
            max = Integer.parseInt(max_str);
        } catch (Exception e) {
            //skip it
            max = 20;
        }
        File dir = null;
        String pathSeparator = System.getProperty("path.separator");
        String unixPathSeparator = ":";
        String rootPath;
        if (unixPathSeparator.equals(pathSeparator)) {
            rootPath = "/";
            if (path.startsWith(rootPath)) {
                dir = new File(path);
            } else {
                dir = new File(genAbsolutePath(path));
            }
        } else {
            rootPath = "^[c-zC-Z]:.*";
            if (path.matches(rootPath)) {
                dir = new File(path);
            } else {
                dir = new File(genAbsolutePath(path));
            }
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static String genAbsolutePath(String path) {
        String[] paths = path.split("/|\\\\");
        URL resource = ClassLoader.getSystemClassLoader().getResource(".");
        String classPath = resource.getPath();
        File file = new File(classPath);
        String resultPath = null;
        boolean isFileName = false;
        for (String p : paths) {
            if (StringUtils.isBlank(p)) {
                continue;
            }
            if (!isFileName) {
                if ("..".equals(p)) {
                    file = file.getParentFile();
                } else if (".".equals(p)) {
                    continue;
                } else {
                    isFileName = true;
                    resultPath = file.getPath() + File.separator + p;
                }
            } else {
                resultPath += File.separator + p;
            }
        }
        return resultPath;
    }

    public static Result createArea(String areaName) {
        return createArea(areaName, null, null);
    }

    public static Result createArea(String areaName, Long cacheSize) {
        return createArea(areaName, cacheSize, null);
    }

    public static Result createArea(String areaName, Comparator comparator) {
        return createArea(areaName, null, comparator);
    }

    public static Result createArea(String areaName, Long cacheSize, Comparator comparator) {
        // prevent too many areas
        if (AREAS.size() > (max - 1)) {
            return new Result(false, "KV_AREA_CREATE_ERROR");
        }
        if (StringUtils.isBlank(areaName)) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        if (AREAS.containsKey(areaName)) {
            return new Result(true, "KV_AREA_EXISTS");
        }
        if (StringUtils.isBlank(dataPath) || !checkPathLegal(areaName)) {
            return new Result(false, "KV_AREA_CREATE_ERROR");
        }
        Result result;
        try {
            File dir = new File(dataPath + File.separator + areaName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filePath = dataPath + File.separator + areaName + File.separator + BASE_DB_NAME;
            DB db = openDB(filePath, true, cacheSize, comparator);
            AREAS.put(areaName, db);
            result = Result.getSuccess();
        } catch (Exception e) {
            Log.error("error create area: " + areaName, e);
            result = new Result(false, "KV_AREA_CREATE_ERROR");
        }
        return result;
    }

    public static Result destroyArea(String areaName) {
        if (!baseCheckArea(areaName)) {
            return new Result(false, "KV_AREA_NOT_EXISTS");
        }
        if (StringUtils.isBlank(dataPath) || !checkPathLegal(areaName)) {
            return new Result(false, "KV_AREA_PATH_ERROR");
        }
        Result result;
        try {
            File dir = new File(dataPath + File.separator + areaName);
            if (!dir.exists()) {
                return new Result(false, "KV_AREA_NOT_EXISTS");
            }
            String filePath = dataPath + File.separator + areaName + File.separator + BASE_DB_NAME;
            destroyDB(filePath);
            AREAS.remove(areaName);
            result = Result.getSuccess();
        } catch (Exception e) {
            Log.error("error destroy area: " + areaName, e);
            result = new Result(false, "KV_AREA_DESTROY_ERROR");
        }
        return result;
    }

    private static void destroyDB(String dbPath) throws IOException {
        File file = new File(dbPath);
        Options options = new Options();
        DBFactory factory = Iq80DBFactory.factory;
        factory.destroy(file, options);
    }

    /**
     * close all area
     * 关闭所有数据区域
     */
    public static void close() {
        Set<Map.Entry<String, DB>> entries = AREAS.entrySet();
        for (Map.Entry<String, DB> entry : entries) {
            try {
                AREAS.remove(entry.getKey());
                entry.getValue().close();
            } catch (IOException e) {
                Log.warn("close leveldb error", e);
            }
        }
    }

    /**
     * @param dbPath
     * @param createIfMissing
     * @return
     * @throws IOException
     */
    private static DB openDB(String dbPath, boolean createIfMissing) throws IOException {
        File file = new File(dbPath);
        Options options = new Options().createIfMissing(createIfMissing);
        DBFactory factory = Iq80DBFactory.factory;
        return factory.open(file, options);
    }

    /**
     * @param dbPath
     * @param createIfMissing
     * @param cacheSize
     * @param comparator
     * @return
     * @throws IOException
     */
    private static DB openDB(String dbPath, boolean createIfMissing, Long cacheSize, Comparator comparator) throws IOException {
        File file = new File(dbPath);
        Options options = new Options().createIfMissing(createIfMissing);
        if(cacheSize != null) {
            options.cacheSize(cacheSize);
        }
        if(comparator != null) {
            DBComparator dbComparator = new DBComparator(){
                public int compare(byte[] key1, byte[] key2) {
                    return comparator.compare(key1, key2);
                }
                public String name() {
                    return "key-comparator";
                }
                public byte[] findShortestSeparator(byte[] start, byte[] limit) {
                    return start;
                }
                public byte[] findShortSuccessor(byte[] key) {
                    return key;
                }
            };
            options.comparator(dbComparator);
        }
        DBFactory factory = Iq80DBFactory.factory;
        return factory.open(file, options);
    }

    private static boolean checkPathLegal(String areaName) {
        if (StringUtils.isBlank(areaName)) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_\\-]+$";
        return areaName.matches(regex);
    }

    private static boolean baseCheckArea(String areaName) {
        if (StringUtils.isBlank(areaName) || !AREAS.containsKey(areaName)) {
            return false;
        }
        return true;
    }

    @Deprecated
    private static byte[] bytes_(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return str.getBytes(NulsConfig.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            return null;
        }
    }

    @Deprecated
    private static String asString_(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, NulsConfig.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            Log.error(e);
            return null;
        }
    }

    public static String[] listArea() {
        int i = 0;
        Enumeration<String> keys = AREAS.keys();
        String[] areas = new String[AREAS.size()];
        int length = areas.length;
        while (keys.hasMoreElements()) {
            areas[i++] = keys.nextElement();
            // thread safe, prevent java.lang.ArrayIndexOutOfBoundsException
            if (i == length) {
                break;
            }
        }
        return areas;
    }

    public static Result put(String area, byte[] key, byte[] value) {
        if (!baseCheckArea(area)) {
            return new Result(true, "KV_AREA_NOT_EXISTS");
        }
        if (key == null || value == null) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        try {
            DB db = AREAS.get(area);
            db.put(key, value);
            return Result.getSuccess();
        } catch (Exception e) {
            return Result.getFailed(e.getMessage());
        }
    }

    public static Result put(String area, String key, String value) {
        if (!baseCheckArea(area)) {
            return new Result(true, "KV_AREA_NOT_EXISTS");
        }
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        try {
            DB db = AREAS.get(area);
            db.put(bytes(key), bytes(value));
            return Result.getSuccess();
        } catch (Exception e) {
            return Result.getFailed(e.getMessage());
        }
    }

    public static Result put(String area, byte[] key, String value) {
        if (!baseCheckArea(area)) {
            return new Result(true, "KV_AREA_NOT_EXISTS");
        }
        if (key == null || StringUtils.isBlank(value)) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        try {
            DB db = AREAS.get(area);
            db.put(key, bytes(value));
            return Result.getSuccess();
        } catch (Exception e) {
            return Result.getFailed(e.getMessage());
        }
    }

    public static Result delete(String area, String key) {
        if (!baseCheckArea(area)) {
            return new Result(true, "KV_AREA_NOT_EXISTS");
        }
        if (StringUtils.isBlank(key)) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        try {
            DB db = AREAS.get(area);
            db.delete(bytes(key));
            return Result.getSuccess();
        } catch (Exception e) {
            return Result.getFailed(e.getMessage());
        }
    }

    public static Result delete(String area, byte[] key) {
        if (!baseCheckArea(area)) {
            return new Result(true, "KV_AREA_NOT_EXISTS");
        }
        if (key == null) {
            return Result.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        try {
            DB db = AREAS.get(area);
            db.delete(key);
            return Result.getSuccess();
        } catch (Exception e) {
            return Result.getFailed(e.getMessage());
        }
    }

    public static byte[] get(String area, String key) {
        if (!baseCheckArea(area)) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            DB db = AREAS.get(area);
            return db.get(bytes(key));
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] get(String area, byte[] key) {
        if (!baseCheckArea(area)) {
            return null;
        }
        if (key == null) {
            return null;
        }
        try {
            DB db = AREAS.get(area);
            return db.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static Set<String> keySet(String area) {
        if (!baseCheckArea(area)) {
            return null;
        }
        DBIterator iterator = null;
        Set<String> keySet = null;
        try {
            DB db = AREAS.get(area);
            keySet = new HashSet<>();
            iterator = db.iterator();
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                keySet.add(asString(iterator.peekNext().getKey()));
            }
            return keySet;
        } catch (Exception e) {
            Log.error(e);
            return null;
        } finally {
            // Make sure you close the iterator to avoid resource leaks.
            if(iterator != null) {
                try {
                    iterator.close();
                } catch (IOException e) {
                    //skip it
                }
            }
        }
    }

    public static Set<Map.Entry<String,String>> entrySet(String area) {
        if (!baseCheckArea(area)) {
            return null;
        }
        DBIterator iterator = null;
        Set<Map.Entry<String,String>> entrySet = null;
        try {
            DB db = AREAS.get(area);
            iterator = db.iterator();
            for(iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                String key = asString(iterator.peekNext().getKey());
                String value = asString(iterator.peekNext().getValue());
                System.out.println(key+" = "+value);
            }

        } catch (Exception e) {
            Log.error(e);
            return null;
        } finally {
            // Make sure you close the iterator to avoid resource leaks.
            if(iterator != null) {
                try {
                    iterator.close();
                } catch (IOException e) {
                    //skip it
                }
            }
        }
        //TODO
        return null;
    }
}
