package com.zzzyi.apidemo.utils;


import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestUtil {

    public static String getParameters() {
        HttpServletRequest request = RequestHolder.getRequest ();
        Enumeration<String> paraNames = Objects.requireNonNull(request).getParameterNames ();
        if (paraNames == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder ();
        while (paraNames.hasMoreElements ()) {
            String paraName = paraNames.nextElement ();
            sb.append ("&").append (paraName).append ("=").append (request.getParameter (paraName));
        }
        return sb.toString ();
    }

    public static Map<String, Object> getParametersMap() {
        HttpServletRequest request = RequestHolder.getRequest ();
        Enumeration<String> paraNames = Objects.requireNonNull(request).getParameterNames ();
        if (paraNames == null) {
            return null;
        }
        Map<String, Object> res = new HashMap<>();
        while (paraNames.hasMoreElements ()) {
            String paraName = paraNames.nextElement ();
            res.put (paraName, request.getParameter (paraName));
        }
        return res;
    }

    public static String getHeader(String headerName) {
        return Objects.requireNonNull(RequestHolder.getRequest()).getHeader (headerName);
    }

    public static String getReferer() {
        return getHeader ("Referer");
    }

    public static String getUa() {
        return getHeader ("User-Agent");
    }

    public static String getIp() {
        HttpServletRequest request = RequestHolder.getRequest ();
        return IPUtil.getIp(Objects.requireNonNull(request));
    }

    public static String getIp(HttpServletRequest request) {
        return IPUtil.getIp(Objects.requireNonNull(request));
    }

    public static String getRequestUrl() {
        HttpServletRequest request = RequestHolder.getRequest ();
        return Objects.requireNonNull(request).getRequestURL ().toString ();
    }

    public static String getMethod() {
        HttpServletRequest request = RequestHolder.getRequest ();
        return Objects.requireNonNull(request).getMethod ();
    }

    public static boolean isAjax(HttpServletRequest request) {
        if (request == null) {
            request = RequestHolder.getRequest ();
        }
        return "XMLHttpRequest".equalsIgnoreCase (request.getHeader ("X-Requested-With"))
                || request.getParameter ("ajax") != null;

    }

}
