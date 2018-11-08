package com.cs.sicnu.core.protocol;

import java.util.HashSet;
import java.util.Set;

public class Http11Constant {

    public final static String post_m="POST";
    public final static String get_m="GET";
    public final static String delete_m="DELETE";
    public final static String put_m="PUT";
    public final static String option_m="OPTION";
    public final static String trace_m="TRACE";
    public final static String connect_m="CONNECT";

    public static final int CONTENT_MAX_SIZE=8*1024*1024;

    public static Set<String> supportVersion = null;

    //common
    public static Set<String> suporrtMethods = null;

    public static Set<String> commonfiled = null;

    public static Set<String> requestfiled = null;

    public static Set<String> responsefiled = null;

    public static Set<String> contentfield = null;




    static {
        supportVersion=new HashSet<>();
        suporrtMethods=new HashSet<>();
        commonfiled=new HashSet<>();
        requestfiled=new HashSet<>();
        responsefiled=new HashSet<>();
        contentfield=new HashSet<>();
    }

    static {
        supportVersion.add("HTTP/1.0");
        supportVersion.add("HTTP/1.1");
    }

    static {
        suporrtMethods.add(post_m);
        suporrtMethods.add(get_m);
        suporrtMethods.add(delete_m);
        suporrtMethods.add(put_m);
        suporrtMethods.add(option_m);
        suporrtMethods.add(trace_m);
        suporrtMethods.add(connect_m);
    }

    static {
        contentfield.add("cache-control");
        contentfield.add("connection");
        contentfield.add("date");
        contentfield.add("pragma");
        contentfield.add("trailer");
        contentfield.add("transfer-encoding");
        contentfield.add("Upgrade");
        contentfield.add("Warning");
    }

    static {
        requestfiled.add("Accept");
        requestfiled.add("Accept-Charset");
        requestfiled.add("Accept-Encoding");
        requestfiled.add("Accept-Language");
        requestfiled.add("Authorization");
        requestfiled.add("Expect");
        requestfiled.add("From");
        requestfiled.add("Host");
        requestfiled.add("If-Match");
        requestfiled.add("If-Modified-Since");
        requestfiled.add("If-None-Match");
        requestfiled.add("If-Range");
        requestfiled.add("If-Unmodified-Since");
        requestfiled.add("Max-Forwards");
        requestfiled.add("Proxy-Authorization");
        requestfiled.add("Referer");
        requestfiled.add("TE");
     }

    static {}

    static {
        contentfield.add("Allow");
        requestfiled.add("Content-Encoding");
        requestfiled.add("Content-Language");
        requestfiled.add("Content-Length");
        requestfiled.add("Content-Location");
        requestfiled.add("Content-MD5");
        requestfiled.add("Content-Range");
        requestfiled.add("Content-Type");
        requestfiled.add("Expires");
        requestfiled.add("Last-Modified");
    }
}
