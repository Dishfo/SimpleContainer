package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class QueryParam extends URLFormParam{
    @Override
    public Map<String, String> parse(HttpServletRequest request) {
        String uri=request.getRequestURI();
        if (StringUtils.isEmpty(uri)){
            return new HashMap<>();
        }
        URI u=URI.create(uri);
        String l=u.getRawQuery();
        try {
            return getMap(l);
        } catch (UnsupportedEncodingException e) {
            return new HashMap<>();
        }
    }
}
