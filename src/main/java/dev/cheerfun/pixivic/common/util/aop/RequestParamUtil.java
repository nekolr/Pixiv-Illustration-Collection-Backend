package dev.cheerfun.pixivic.common.util.aop;

import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RequestParamType;
import dev.cheerfun.pixivic.common.context.AppContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/12/3 8:25 AM
 * @description RequestUtil
 */
@Component
public class RequestParamUtil {
    public HttpServletRequest getCurrentRequest() throws IllegalStateException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return attrs.getRequest();
    }

    public String queryRequestParam(String requestParamType, String requestParamKey) {
        HttpServletRequest currentRequest = getCurrentRequest();
        switch (requestParamType) {
            case RequestParamType
                    .HEADER:
                return currentRequest.getHeader(requestParamKey);
            case RequestParamType.URL_PARAM:
                return currentRequest.getParameter(requestParamKey);
            default:
                return null;
        }
    }

    public int queryRealIp() {
        if (AppContext.get() != null) {
            Object ip = AppContext.get().get(AuthConstant.X_FORWARDED_FOR);
            if (ip != null) {
                return (int) ip;
            }
        }
        String xForwardedFor = queryRequestParam(RequestParamType.HEADER, AuthConstant.X_FORWARDED_FOR);
        int i = xForwardedFor.indexOf(",");
        int ip;
        if (i == -1) {
            ip = ip2Int(xForwardedFor);
        } else {
            ip = ip2Int(xForwardedFor.substring(0, i));
        }
        //ip地址转int节省内存

        //放进threadLocal
        if (AppContext.get() != null) {
            AppContext.get().put(AuthConstant.X_FORWARDED_FOR, ip);
        }
        return ip;
    }

    public Integer queryUserIdFromAppContext() {
        if (AppContext.get() != null && AppContext.get().get(AuthConstant.USER_ID) != null) {
            return (Integer) AppContext.get().get(AuthConstant.USER_ID);
        }
        return null;
    }

    public int ip2Int(String ipString) {
        // 取 ip 的各段
        String[] ipSlices = ipString.split("\\.");
        int rs = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            int intSlice = Integer.parseInt(ipSlices[i]) << 8 * i;
            // 或运算
            rs = rs | intSlice;
        }
        return rs;
    }
}
