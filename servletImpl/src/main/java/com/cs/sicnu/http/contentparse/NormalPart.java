package com.cs.sicnu.http.contentparse;

import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.utils.BytesServletInputStream;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * 表示一个非文件类型的part
 * 一般用于post的参数传递
 */
public class NormalPart implements Part {

    private HashMap<String,String> headers=new HashMap<>();
    protected byte[] data;
    protected String boundery;
    protected int content_length;
    protected String partName;
    protected String contentType;
    boolean isfile;

    @Override
    public InputStream getInputStream() throws IOException {
        return new BytesServletInputStream(data);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void addHeader(String name,String value){

    }

    @Override
    public String getName() {
        return partName;
    }

    @Override
    public String getSubmittedFileName() {
        return "";
    }

    @Override
    public long getSize() {
        return 0;
    }


    @Override
    public void write(String fileName) throws IOException {
        throw new UnsupportedOperationException("write(String fileName)");
    }

    @Override
    public void delete() throws IOException {

    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        String val=headers.get(name);
        if (val==null){
            return null;
        }else {
            return Arrays.asList(val.split(HttpHeadConstant.HEAD_Separator));
        }
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }
}
