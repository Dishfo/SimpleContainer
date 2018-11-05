package com.sicnu.cs.servlet.http;

import com.cs.sicnu.core.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class URLFormParam implements ParameterParser {
    @Override
    public Map<String, String> parse(HttpServletRequest request) {
        StringBuilder builder=new StringBuilder();
        Map<String,String> stringMap=new HashMap<>();
        String s;
        try {
            BufferedReader reader=request.getReader();
            if ((s=reader.readLine())!=null){
                builder.append(s);
            }
            stringMap.putAll(getMap(builder.toString()));

        } catch (IOException e) {}
        return stringMap;
    }

    protected Map<String,String> getMap(String s) throws UnsupportedEncodingException {
        Map<String,String> map=new HashMap<>();
        if (StringUtils.isEmpty(s)){
            return map;
        }
        String[] pairs = s.split("\\&");
        for (int i = 0; i < pairs.length; i++) {
            String[] fields = pairs[i].split("=");
            String name = URLDecoder.decode(fields[0], "UTF-8");
            String value = URLDecoder.decode(fields[1], "UTF-8");
            map.put(name,value);
        }

        return map;
    }

}
