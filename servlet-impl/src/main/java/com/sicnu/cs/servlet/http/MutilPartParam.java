package com.sicnu.cs.servlet.http;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutilPartParam implements ParameterParser {
    @Override
    public Map<String, String> parse(HttpServletRequest request) {
        List<FileItem> multiparts;
        Map<String,String> map=new HashMap<>();
        try {
            multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        } catch (FileUploadException e) {
            return map;
        }
        for (FileItem item : multiparts) {
            if (item.isFormField()){
                String name=item.getFieldName();
                byte[] bytes=item.get();
                try {
                    map.put(name,new String(bytes,request.getCharacterEncoding()));
                } catch (UnsupportedEncodingException e) {
                    return map;
                }
            }
        }
        return map;
    }
}
