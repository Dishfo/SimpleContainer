package com.cs.sicnu.core.protocol;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public final class StatusConstant {
    private static final HashMap<Integer,String> msgMap=new HashMap<>();

    static {
        msgMap.put(HttpServletResponse.SC_NOT_FOUND,"Not Found");
        msgMap.put(HttpServletResponse.SC_OK,"OK");
        msgMap.put(HttpServletResponse.SC_SEE_OTHER,"SEE_OTHER");
        msgMap.put(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"INTERAL_ERROR");
    }

    public static String  getMsg(int sc){
        return msgMap.get(sc);
    }

}
