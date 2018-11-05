package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class CookFactory {

    static List<Cookie> create(Map<String,String> cookMap, SessionCookieConfig cookieConfig){
        List<Cookie> cookies=new ArrayList<>();
        cookMap.forEach((k, v) -> {
            Cookie cookie=new Cookie(k,v);

            if (cookieConfig!=null){
                cookie.setDomain(cookieConfig.getDomain());
                cookie.setComment(cookieConfig.getComment());
                cookie.setPath(cookieConfig.getPath());
                cookie.setHttpOnly(cookieConfig.isHttpOnly());
                cookie.setSecure(cookieConfig.isSecure());
                cookie.setMaxAge(cookieConfig.getMaxAge());
            }

            cookies.add(cookie);
        });
        return cookies;
    }

    static List<Cookie> create(Map<String,String> cookMap){
        return create(cookMap,null);
    }

    private final static String cookie_split="; ";
    public static String resolve(Cookie cookie){
        StringBuilder builder=new StringBuilder();
        builder.append(cookie.getName())
                .append("=")
                .append(cookie.getValue());
        int maxAge=cookie.getMaxAge();
        String path=cookie.getPath();
        String domain=cookie.getDomain();
        boolean httponly=cookie.isHttpOnly();
        boolean secure=cookie.getSecure();

        if (maxAge>0){
            builder.append(cookie_split)
                    .append(HttpHeadConstant.COOK_MAX_AGE)
                    .append("=").append(maxAge);
        }

        if (path!=null){
            builder.append(cookie_split)
                    .append(HttpHeadConstant.COOK_PATH)
                    .append("=").append(path);
        }

        if (domain!=null){
            builder.append(cookie_split)
                    .append(HttpHeadConstant.COOK_DOMAIN)
                    .append("=").append(domain);
        }

        if (httponly){
            builder.append(cookie_split)
                    .append(HttpHeadConstant.COOK_HTTPONLY);
        }

        if (secure){
            builder.append(cookie_split)
                    .append(HttpHeadConstant.COOK_SECURE);
        }
        return builder.toString();
    }
}
