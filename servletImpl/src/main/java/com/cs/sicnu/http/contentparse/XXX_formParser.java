package com.cs.sicnu.http.contentparse;

import com.cs.sicnu.core.protocol.ParseException;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XXX_formParser {

    private byte[] cache = new byte[1024 * 1024 * 8];

    public Map<String, String[]> parse(ServletInputStream inputStream) throws ParseException, IOException {
        Map<String, String[]> map = new HashMap<>();
        String s=null;
        int n;
        if (inputStream.available()>1024*1024*8){
            throw new IOException("length of data is too long ");
        }
        if ((n = inputStream.read(cache)) > 0) {
             s=new String(cache,0,n);
        } else {
            throw new IOException("read content error");
        }

        String[] items=s.split("&");
        for (String item:items){
            String[] vn=item.split("=");
            if (vn.length!=2){
                throw new ParseException("content format error");
            }

            map.put(vn[0],vn[1].split(","));
        }

        return map;
    }
}
