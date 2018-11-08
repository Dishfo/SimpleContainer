package com.cs.sicnu.core.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HeapAcesss implements ByteAcess {

    private static final int MAX_SIZE=1024*1024*8;

    private byte[] buf;

    private int ost;

    private int limit;
    private int capity;

    public HeapAcesss(){
        this(MAX_SIZE);
    }

    private HeapAcesss(int capity){
        this.capity=capity;
        limit=ost=0;
        buf=new byte[capity];
    }

    @Override
    public void append(ByteBuffer buffer) throws IllegalAccessException {
        if (buffer.limit()+limit>capity){
            throw new IllegalAccessException("the accessor can't accept " +
                    "the data");
        }

        System.arraycopy(buffer.array(),0,buf,limit,buffer.limit());
        limit+=buffer.limit();
    }

    @Override
    public void append(byte[] bytes, int offset, int len) throws IllegalAccessException {
        if (limit+(len)>capity){
            throw new IllegalAccessException("the accessor can't accept " +
                    "the data");
        }

        System.arraycopy(bytes,offset,buf,limit,len);
        limit+=len;
    }

    @Override
    public void mark(int cur) {
        ost +=cur;
    }

    @Override
    public void forward(int offset) {
        int torm=this.ost+offset;
        byte[] newbuf=new byte[capity];
        System.arraycopy(buf,torm,newbuf,0,(limit-torm));
        limit=limit-torm;
        ost=0;
        buf=newbuf;
    }

    @Override
    public void clear() {
        limit= ost =0;
    }

    @Override
    public int find(byte[] str) {
        return find(str,0);
    }

    @Override
    public int find(byte[] str, int offset) {
        for (int i=offset+ost;i<=limit-str.length;i++){
            if (buf[i]==str[0]){
                int j=0;

                for (;j<str.length;j++){
                    if (str[j]!=buf[j+i]){
                        break;
                    }
                }

                if (j>=str.length){
                    return i-ost;
                }
            }
        }
        return -1;
    }

    /**
     * 返回当前可用的缓存长度
     * @return 返回当前可用的缓存长度
     */
    @Override
    public int getBound() {
        return limit-ost;
    }

    @Override
    public String getString(int begin, int end, Charset charset) {
        return new String(buf,begin+ost,(end-begin),charset);
    }

    @Override
    public byte[] getRangeBytes(int offset, int len) {
        byte[] bytes=new byte[len];
        for (int i=0;i<len;i++){
            bytes[i]=buf[i+offset+ost];
        }
        return bytes;
    }
}
