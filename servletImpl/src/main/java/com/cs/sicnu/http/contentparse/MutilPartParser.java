package com.cs.sicnu.http.contentparse;

import com.cs.sicnu.core.protocol.HeadLine;
import com.cs.sicnu.core.protocol.HeadLine.ValueAdorn;
import com.cs.sicnu.core.protocol.HeadLine.ValueDescribe;
import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.protocol.ParseException;
import com.cs.sicnu.core.utils.BytesServletInputStream;
import com.cs.sicnu.core.utils.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MutilPartParser {

    private static final int NONE=0;
    private static final int PARTING=0x1;
    private static final int PARTHEAD=0x2;
    private static final int PARTBODY=0x3;
    private static final int PARTEND=0x4;
    private static final int END=0x5;

    private String s_boundary=null;
    private String e_boundary=null;
    private ServletInputStream inputStream;

    public List<Part> parse(byte[] content,int len,String  boundry) throws ParseException {
        return parse(new BytesServletInputStream(content),len,boundry);
    }

    private int state=NONE;
    public List<Part> parse(ServletInputStream content, int len, String boundry)throws ParseException {

        List<Part> parts=new ArrayList<>();
        state=NONE;
        inputStream=content;
        if (boundry==null||boundry.equals("")){
            throw new ParseException("error boundary");
        }

        try {
            headVertify(inputStream,boundry);
        } catch (IOException e) {
            throw new ParseException("the content inputstream error");
        }

        s_boundary="--"+boundry;
        e_boundary="--"+boundry+"--";

        String line;
        byte[] bytes=new byte[len];
        int n;

        NormalPart part=null;
        //part head parse
        try {

            while (state!=END&&
                    (n=inputStream.readLine(bytes,0,len))>0){
                line=new String(bytes,0,n-2);
                if (line.equals("")){
                    if (state==PARTHEAD){
                        state++;
                        byte[] body=parseContent();
                        part.data=body;
                        part.content_length=body.length;
                        parts.add(part);
                        part=null;
                    }else {
                        throw new ParseException("content format error");
                    }
                }else if (line.equals(s_boundary)){
                    throw new ParseException("content format error");
                }else {
                    if (state==PARTING){
                        state=PARTHEAD;
                        part=new NormalPart();
                        part.boundery=boundry;
                    }
                    HeadLine headLine=HeadLine.createHeadLine(line);
                    fillPartObject(part,headLine);
                }
            }
        }catch (IOException e){
            throw new ParseException("content stream error");
        }

        //part content parse
        return parts;
    }

    private static final int CACHE_LEN=1024*1024*8;
    private byte[] cache=new byte[CACHE_LEN];

    private byte[] parseContent() throws ParseException, IOException {

        if (state!=PARTBODY){
            throw new ParseException("content format error");
        }

        boolean compete=false;
        String line;
        int front,end,n;
        front=end=0;

        while ((n=inputStream.readLine(cache,front,CACHE_LEN-front))>0){
            line=new String(cache,front,n);
            if (line.equals(s_boundary+"\r\n")){
                state=PARTING;
                compete=true;
                break;
            }else if (line.equals(e_boundary+"\r\n")){
                state=END;
                compete=true;
                break;
            }
            front+=n;
        }
        if (compete){
            byte[] body=new byte[front-2];
            System.arraycopy(cache,0,body,0,front-2);
            return body;
        }else {
            throw new ParseException("content format error");
        }
    }

    private void fillPartObject(NormalPart part,HeadLine line) throws ParseException {
        String name=line.getName();
        if (name.compareToIgnoreCase(HttpHeadConstant.H_CONT_DISP)==0){
            Iterator<ValueDescribe> itdes=line.describeIterable();
            if (!itdes.hasNext()){
                throw new ParseException("content format error");
            }
            ValueDescribe describe=itdes.next();
            if (!describe.getBaseVal().equals("form-data")){
                throw new ParseException("content frmat error");
            }
            Iterator<ValueAdorn> itad=describe.adornIterator();
            while (itad.hasNext()){
                ValueAdorn adorn=itad.next();
                String adorname=adorn.getName();
                if (adorname.trim().equals("name")){
                    part.partName= StringUtils.getStringRquito(adorn.getVlaue());
                }else if (adorname.trim().equals("filename")){
                    part.isfile=true;
                }
            }
        } else if(name.compareToIgnoreCase(HttpHeadConstant.H_CONT_TYPE)==0){
            part.contentType=line.getValue();
        }
        part.addHeader(line.getName(),line.getValue());
    }


    /**
     * 检验是否为boundery开头
     *
     * @param inputStream c以流的形式访问body
     */
    private void headVertify(ServletInputStream inputStream,String boundary) throws IOException,ParseException {
        byte[] bytes=new byte[boundary.length()*2+2];
        try {
            int n=inputStream.readLine(bytes,0,bytes.length);
            String s=new String(bytes,0,n-2);
            if (!s.equals("--"+boundary)){
                throw new ParseException("mutipart content format error");
            }
            state++;
        } catch (IOException e) {
            throw e;
        }
    }
}
