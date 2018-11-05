package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CookieParse implements ParameterParser {
    @Override
    public Map<String, String> parse(HttpServletRequest request) {
        Map<String,String> cookies=new HashMap<>();
        String val=request.getHeader(HttpHeadConstant.H_COOKIE);
        if (val!=null){
            String cok[]=val.split(";");
            for (String cook:cok){
                String[] map=cook.trim().split("=");
                if (map.length!=2){
                    throw new IllegalArgumentException("miss val or name");
                }
                cookies.put(map[0].trim(),map[1].trim());
            }
        }
        return cookies;
    }
}
