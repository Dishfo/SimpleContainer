package com.sicnu.cs.servlet.basis;

import org.junit.Test;

import java.net.URI;
import java.util.Locale;

public class ServletTest {

    @Test
    public void test(){
        String s="http://dwad/%E5%87%A0%E5%8F%B7";
        URI uri=URI.create(s);
        System.out.println(uri.getPath());
        Locale locale=new Locale("zh","CN");
        System.out.println(locale.getCountry() + locale.getLanguage());
    }
}













