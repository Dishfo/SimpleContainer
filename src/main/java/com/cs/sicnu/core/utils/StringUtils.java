package com.cs.sicnu.core.utils;

import java.util.Objects;

public final class StringUtils {

    public static boolean isEqual(String s1, String s2) {
        Objects.requireNonNull(s1);
        Objects.requireNonNull(s2);

        if (s1.length() != s2.length()) {
            return false;
        }

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if (c1 != c2 && (Math.abs(c1 - c2)) != 32) {
                return false;
            }
        }

        return true;
    }

    public static String convertToRegex(String s) {
        Objects.requireNonNull(s);
        StringBuilder builder = new StringBuilder();
        int len = s.length();

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '.':
                    builder.append("\\.");
                    break;
                case '*':
                    builder.append(".+");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }

        return builder.toString();
    }

    public static String getStringRquito(String s){
        String res=s;
        if (s.startsWith("\"")){
            res=s.substring(1);
        }

        if (s.endsWith("\"")){
            res=res.substring(0,res.length()-1);
        }

        return res;
    }

}
