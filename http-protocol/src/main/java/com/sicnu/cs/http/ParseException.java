package com.sicnu.cs.http;

public class ParseException extends Exception{

    private ParseType parseType;

    public ParseException(String message,ParseType parseType) {
        super(message);
        this.parseType=parseType;
    }

    public ParseType getParseType() {
        return parseType;
    }
}
