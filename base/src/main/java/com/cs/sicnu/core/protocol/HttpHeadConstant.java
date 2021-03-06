package com.cs.sicnu.core.protocol;

import org.omg.CORBA.PUBLIC_MEMBER;

public final class HttpHeadConstant {
    public static final String HEAD_Separator = ",";

    public static final String CT_TYPE_XXX = "application/x-www-form-urlencoded";
    public static final String CT_TYPE_form = "multipart/form-data";
    public static final String CT_TYPE_text = "text/plain";

    public static final String H_CONT_DISP = "content-disposition";
    public static final String H_HOST = "host";
    public static final String H_CONT_LEN = "content-length";
    public static final String H_CONT_TYPE = "content-type";
    public static final String H_CONT_ENCODING = "content-encoding";
    public static final String H_CONT_LANG = "content-language";
    public static final String H_LOC = "location";
    public static final String H_APT_LANG = "accept-anguage";
    public static final String H_COOKIE = "cookie";

    public static final String H_CONN="connection";
    public static final String H_CONE_GZIP = "gzip";
    public static final String H_CONE_COMPRESS = "compress";
    public static final String H_CONE_DEFLATE = "deflate";
    public static final String H_CONE_IDENTITY = "identity";

    //response
    public static final String H_SET_COOKIE = "set-Cookie";

    public static final String head_value_split = ";";
    public static final String head_split = ": ";

    public static final String COOK_MAX_AGE="Max-Age";
    public static final String COOK_DOMAIN="Domain";
    public static final String COOK_PATH="Path";
    public static final String COOK_SECURE="Secure";
    public static final String COOK_HTTPONLY="HttpOnly";

    public static final String CONN_KEEP_ALIVE="Keep-Alive";
    public static final String CONN_CLOSE="close";

}
