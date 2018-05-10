/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.client.rpc.filter;

import io.nuls.client.rpc.constant.RpcConstant;
import io.nuls.core.tools.log.Log;
import io.nuls.core.tools.str.StringUtils;
import io.nuls.kernel.cfg.NulsConfig;
import io.nuls.kernel.constant.KernelErrorCode;
import io.nuls.kernel.exception.NulsRuntimeException;
import io.nuls.kernel.model.Result;
import org.glassfish.grizzly.http.server.Request;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.IOException;

/**
 * @author Niels
 * @date 2017/9/28
 */
public class RpcServerFilter implements ContainerRequestFilter, ContainerResponseFilter, ExceptionMapper<Exception> {

    @Inject
    private Provider<Request> grizzlyRequestProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HttpContextHelper.put(grizzlyRequestProvider.get());
        if (!whiteSheetVerifier(grizzlyRequestProvider.get())) {
            throw new NulsRuntimeException(KernelErrorCode.REQUEST_DENIED);
        }
        requestContext.setProperty("start", System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext){
//        Log.info("url:{},IP:{},useTime:{}, params:{},result:{}", requestContext.getUriInfo().getRequestUri().getPath() + "?" + requestContext.getUriInfo().getRequestUri().getQuery(), grizzlyRequestProvider.get().getRemoteAddr()
//                , (System.currentTimeMillis() - Long.parseLong(requestContext.getProperty("start").toString())), null, responseContext.getEntity());
    }

    @Override
    public Response toResponse(Exception e) {
        Log.error(e);
        Result result = Result.getFailed().setMessage(e.getMessage());
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }

    private boolean whiteSheetVerifier(Request request) {
        String ips = null;
        try {
            ips = NulsConfig.MODULES_CONFIG.getCfgValue(RpcConstant.CFG_RPC_SECTION, RpcConstant.CFG_RPC_REQUEST_WHITE_SHEET);
        } catch (Exception e) {
            Log.error(e);
        }
        if (StringUtils.isBlank(ips)) {
            return false;
        }
        String[] ipArray = ips.split(RpcConstant.WHITE_SHEET_SPLIT);
        String realIp = request.getRemoteAddr();
        for (String ip : ipArray) {
            if (ip.equals(realIp)) {
                return true;
            }
        }
        return false;
    }
}
