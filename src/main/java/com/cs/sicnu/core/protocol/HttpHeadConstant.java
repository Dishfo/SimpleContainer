package com.cs.sicnu.core.protocol;

public final class HttpHeadConstant {
    public static final String HEAD_Separator=",";

    public static final String CT_TYPE_XXX="application/x-www-form-urlencoded";
    public static final String CT_TYPE_form="multipart/form-data";
    public static final String CT_TYPE_text="text/plain";

    public static final String H_CONT_DISP="content-disposition";
    public static final String H_CONT_LEN="content-length";
    public static final String H_CONT_TYPE="content-type";
    public static final String H_CONT_ENCODING="content_encoding";
    public static final String H_LOC="location";
    public static final String H_COOKIE="cookie";

    public static final String H_CONE_GZIP="gzip";
    public static final String H_CONE_COMPRESS="compress";
    public static final String H_CONE_DEFLATE="deflate";
    public static final String H_CONE_IDENTITY="identity";

    //response
    public static final String H_SET_COOKIE="set-Cookie";


    public static final String head_split=": ";

}
