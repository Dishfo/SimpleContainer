package com.sicnu.cs.http;

import com.cs.sicnu.core.protocol.Http11Constant;
import com.cs.sicnu.core.protocol.HttpHeadConstant;
import com.cs.sicnu.core.utils.ByteAcess;
import com.cs.sicnu.core.utils.HeapAcesss;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 非线程安全的解析类
 */

public class HttpParser {
    private ByteAcess byteAcess;
    private HttpParseListener listener;

    private static final int NONE = 0x1;
    private static final int HEADLINE = 0x2;
    private static final int HEAD = 0x3;
    private static final int BODY = 0x4;
    private static final int ERROR = 0x5;
    private static final int COMPETE = 0x6;

    private int pre_state;
    private ParseException exception;
    private HttpRequest request;

    private int cur, lined,
            load, all,
            state;

    public HttpParser(HttpParseListener listener) {
        this.listener = listener;
        byteAcess = new HeapAcesss();
        state = NONE;
    }

    private String headline=null;
    public void resolve(ByteBuffer buffer) {
        try {
            byteAcess.append(buffer);
        } catch (Exception e) {
            setException(ParseType.buffer_err);
        }

        String line;
        switch (state) {
            case NONE:
                lined = -2;
                line = getLine();
                while (line!=null&&!isVaildHeadLine(line))
                    line = getLine();
                if (line == null){
                    setException(ParseType.no_http);
                    byteAcess.clear();
                    onBufferAll();
                }else {
                    headline=line;
                    state++;
                }

            case HEADLINE:
                if (state==HEADLINE){
                    request=new HttpRequest();
                    parseHeadLine(headline);
                    listener.onStartParse(request);
                }
            case HEAD:
                while (state==HEAD){
                    line=getLine();
                    if (line==null){
                        onBufferAll();
                        break;
                    }
                    parseHead(line);
                }
            case BODY:
                if (state==BODY){
                    parseBody();
                    onBufferAll();
                }
            case ERROR:
                if(state==ERROR){
                    listener.onException(exception, pre_state);
                    clearException();
                }
            case COMPETE:
                if (state==COMPETE){
                    listener.onCompete(request);
                    clearRequset();
                }
        }
    }

    private static final Charset utf_8set = Charset.forName("UTF-8");
    private static final byte[] crnl = "\r\n".getBytes();

    private String getLine() {
        cur = lined + 2;
        lined = byteAcess.find(crnl, cur);
        if (lined == -1) {
            lined=cur-2;
            return null;
        } else {
            return byteAcess.getString(cur,lined,utf_8set);
        }
    }

    private boolean isVaildHeadLine(String line) {
        String[] strings = line.split(" ");
        if (strings.length != 3) {
            return false;
        }
        boolean vaildm = false;
        if (Http11Constant.suporrtMethods.contains(strings[0])) {
            vaildm = true;
        }
        return vaildm;
    }


    private void parseHeadLine(String line){
        String items[]=line.split(" ");
        request.setMethod(items[0]);
        request.setUrl(items[1]);
        request.setHTTPVersion(items[2]);
        state++;
    }


    private void parseHead(String line){

        if (line.equals("")){
            byteAcess.mark(lined+2);
            cur=0;
            state++;
            return;
        }

        int split=line.indexOf(HttpHeadConstant.head_split);
        if (split==-1){
            setException(ParseType.head_format);
        }else {
            String name=line.substring(0,split);
            String value=line.substring(split+2);
            if (name.compareToIgnoreCase(HttpHeadConstant.H_CONT_LEN)==0){
                try {
                    request.setContentLength(Integer.parseInt(value));
                }catch (Throwable t){
                    setException(ParseType.head_format);
                }
            }
            request.setHeader(name.toLowerCase(),value);
        }
    }

    private byte[] cache=null;
    private void parseBody(){

        if (cache==null){
            all=request.getContentLength();
            load=0;
            if (all<0){
                setException(ParseType.lenght_err);
                return;
            }else if (all==0){
                state=COMPETE;
                return;
            }else {
                cache=new byte[all];
            }
        }

        cur=0;
        int bound=byteAcess.getBound();
        byte[] tmp;
        if (bound>=(all-load)){
            tmp=byteAcess.getRangeBytes(0,all-load);
            System.arraycopy(tmp,0,cache,load,all-load);
            cur+=all-load;
            request.setData(cache);
            cache=null;
            state=COMPETE;
        }else {
            tmp=byteAcess.getRangeBytes(0,bound);
            System.arraycopy(tmp,0,cache,load,bound);
            load+=bound;
            cur+=bound;
        }
    }

    /**
     *
     * 遍历完当前的缓存数据时调用
     *
     *
     */
    private void onBufferAll(){
        byteAcess.mark(cur);
    }

    private void setException(ParseType type){
        pre_state=state;
        state=ERROR;
        exception=new ParseException("",type);
    }


    private void clearRequset() {
        request = null;
        state = NONE;
        byteAcess.forward(0);
    }


    private void clearException() {
        exception = null;
        state = NONE;

    }
}
