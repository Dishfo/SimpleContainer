package com.sicnu.cs.http;

public enum ParseType {
    no_http,
    head_format,
    lenght_err,
    buffer_err;

    @Override
    public String toString() {
        switch (this){
            case no_http:
                return "no_http";
            case lenght_err:
                return "length_err";
            case head_format:
                return "head_format";
            case buffer_err:
                return "buffer_err";
                default:
                    return "";
        }
    }
}
