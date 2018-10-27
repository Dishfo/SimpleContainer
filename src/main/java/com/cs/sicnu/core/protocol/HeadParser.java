package com.cs.sicnu.core.protocol;


import com.cs.sicnu.core.protocol.HeadLine.*;

import java.util.List;

/**
 *
 * 用与解析头部的一个工具类
 *
 */
public class HeadParser {

    public HeadLine parseHearLine(String line) throws LineSynatxException {
        HeadLine headLine=new HeadLine();
        int eq_index=line.indexOf(": ");
        if (eq_index==-1){
            throw new LineSynatxException();
        }
        String name=line.substring(0,eq_index);
        String value=line.substring(eq_index+1);
        headLine.setName(name);
        headLine.setValue(value);

        return headLine;
    }

    public List<ValueDescribe> parseValue(String value) throws LineSynatxException{



        return null;
    }

    public List<ValueAdorn> parseVlaue(String vlaue) throws LineSynatxException{
        return null;
    }

    public static class LineSynatxException extends Exception{

    }


}
