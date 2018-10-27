package com.cs.sicnu.core;

import com.cs.sicnu.core.protocol.HeadLine;
import com.cs.sicnu.core.protocol.ParseException;
import com.cs.sicnu.core.utils.BytesServletInputStream;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadParseTest {

    public String request_file="/home/dishfo/logs/request--1538556552050";

    String name_p="^.+: ";
    String value_p="[^,]+(;.+=.+){0}[,|, ]{0,1}";
    String adron_p=";[A-Za-z]+=[^,;]+";

    @Test
    public void regexTest(){

        HeadLine line1=null;
        String line="Content-Disposition: form-data; name=testfield1";
        try {
            line1=HeadLine.createHeadLine(line);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("dwadawda");
    }

    public void test(){
        File file=new File(request_file);
        try {
            FileInputStream inputStream=new FileInputStream(file);
            byte[] bytes=new byte[inputStream.available()];
            inputStream.read(bytes);
            BytesServletInputStream bytesServletInputStream=
                    new BytesServletInputStream(bytes);
            BufferedReader reader=new BufferedReader(
                    new InputStreamReader(bytesServletInputStream, Charset.forName("UTF-8")));
            String line=null;
            while ((line=reader.readLine())!=null){
                Matcher matcher = Pattern.compile(name_p).matcher(line);
                matcher.matches();
                int n=matcher.groupCount();
                if (n>0){
                    String name=matcher.group();
                    String val=line.substring(name.length());
                    System.out.println(val);
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
