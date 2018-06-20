package io.nuls.contract.vm;

import io.nuls.contract.vm.code.ClassCode;
import io.nuls.contract.vm.code.ClassCodeLoader;
import io.nuls.contract.vm.code.FieldCode;
import io.nuls.contract.vm.code.MethodCode;
import io.nuls.contract.vm.util.Log;
import io.nuls.contract.vm.util.Utils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodArea {

    private static final String[] IGNORE_CLINIT;

    static {
        IGNORE_CLINIT = new String[]{};
    }

    private final VM vm;

    private final Map<String, ClassCode> classCodes = new LinkedHashMap<>();

    public MethodArea(VM vm) {
        this.vm = vm;
    }

    public MethodArea(VM vm, MethodArea methodArea) {
        this.vm = vm;
        this.classCodes.putAll(methodArea.classCodes);
    }

    public MethodCode loadMethod(String className, String methodName, String methodDesc) {
        ClassCode classCode = loadClass(className);
        MethodCode methodCode = classCode.getMethodCode(methodName, methodDesc);
        if (methodCode == null && classCode.getSuperName() != null) {
            methodCode = loadSuperMethod(classCode.getSuperName(), methodName, methodDesc);
        }
        if (methodCode == null) {
            for (String interfaceName : classCode.getInterfaces()) {
                methodCode = loadMethod(interfaceName, methodName, methodDesc);
                if (methodCode != null) {
                    break;
                }
            }
        }
        return methodCode;
    }

    private MethodCode loadSuperMethod(String className, String methodName, String methodDesc) {
        ClassCode classCode = loadClass(className);
        MethodCode methodCode = classCode.getMethodCode(methodName, methodDesc);
        if (methodCode == null && classCode.getSuperName() != null) {
            methodCode = loadSuperMethod(classCode.getSuperName(), methodName, methodDesc);
        }
        return methodCode;
    }

    public ClassCode loadClass(String className) {
        className = Utils.classNameReplace(className);
        if (!this.classCodes.containsKey(className)) {
            ClassCode classCode = ClassCodeLoader.loadFromResource(className);
            loadClassCode(classCode);
        }
        return this.classCodes.get(className);
    }

    public void loadClassCodes(Collection<ClassCode> classCodes) {
        if (classCodes != null) {
            for (ClassCode classCode : classCodes) {
                loadClassCode(classCode);
            }
        }
    }

    public void loadClassCode(ClassCode classCode) {
        String className = classCode.getName();
        if (!this.classCodes.containsKey(className)) {
            this.classCodes.put(className, classCode);
            Log.loadClass(className);
            clinit(classCode);
        }
    }

    private void clinit(ClassCode classCode) {
        if (ArrayUtils.contains(IGNORE_CLINIT, classCode.getName())) {
            return;
        }

        for (FieldCode fieldCode : classCode.getFields()) {
            if (fieldCode.isStatic() && fieldCode.isNotFinal()) {
                this.vm.getHeap().putStatic(classCode.getName(), fieldCode.getName(), fieldCode.getVariableType().getDefaultValue());
            }
        }

        MethodCode methodCode = classCode.getMethodCode("<clinit>", "()V");
        if (methodCode != null) {
            this.vm.run(methodCode, null);
        }
    }

}
