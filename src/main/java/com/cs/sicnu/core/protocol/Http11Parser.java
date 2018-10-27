package com.cs.sicnu.core.protocol;

import com.cs.sicnu.core.process.Poster;
import com.cs.sicnu.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * only belong a tcp connection
 * parse unit a complex module
 */

//todo 存在问题 准备重写
public class Http11Parser {

    private static final String LINE_SEP="\r\n";

    private Logger logger = LogManager.getLogger(getClass().getName()) ;

    private HttpRequest curRequest;
    private ByteAcess acess;
    private int cur=0;
    private int linend=-2;

    private Poster<HttpRequest> poster;

    private static final int NONE=0x1;
    private static final int HEADLINE=0x2;
    private static final int HEAD=0x3;
    private static final int BODY=0x4;

    private int stage=NONE;

    private Throwable throwable=null;


    public Http11Parser(){
        acess=new HeapAcesss();
        curRequest=new HttpRequest();
    }

    public void resolve(ByteBuffer data) throws ParseException {
        cur=0;
        linend=-2;
        try {
            acess.append(data);
        }catch (Throwable throwable){
            this.throwable=throwable;
            return;
        }
        String line;
        switch (stage){
            case NONE:
                stage++;
                curRequest=new HttpRequest();
            case HEADLINE:
                line=getHeadLine();
                logger.debug("head line "+line);
                if (line==null){
                    acess.mark(cur);
                    break;
                }else {
                    parseHeadLine(line);
                }
            case HEAD:
                while (stage==HEAD){
                    line=getNextLine();
//                    System.out.println(line);
                    if (line==null){
                        acess.mark(cur);
                        break;
                    }else {
                        parseHead(line);
                    }
                }
            case BODY:
                if (stage==BODY){
                    parseBody();
                }
        }

        if (stage==NONE){
            if (throwable!=null){
                acess.forward(cur);
                cur=0;
                throw new ParseException(throwable.getMessage());
            }else {
                logger.debug("end request");
                acess.forward(cur);
                cur=0;
                linend=-2;
                post(curRequest);
            }

        }
    }

    public void setPoster(Poster<HttpRequest> poster){
        this.poster=poster;
    }

    public void  resolve(ByteBuffer[] datas) throws ParseException {
        for (ByteBuffer b:datas){
            resolve(b);
        }
    }

    private void parseHeadLine(String line) {
        String items[]=line.split(" ");

        if (items.length!=3){
            throwable=new ParseException(" the head line " +
                    "must contain three part");
        }else {
            curRequest.setMethod(items[0]);
            curRequest.setResUrl(items[1]);
            curRequest.setVersion(items[2]);
        }
        stage++;
    }

    private void parseHead(String line){
       // logger.debug(line);
        String map[]=line.split(": ");

        if (line.equals("")) {
            stage++;
            if (curRequest.getContent_length()==0){
                stage=NONE;
            }

            return;
        }

        if (map.length!=2){
            logger.debug("invaild line "+ line);
                throwable=new ParseException(" the head attributies " +
                    "must contain two part");
            stage=NONE;
        }else {
            if (StringUtils.isEqual("cookie",map[0])){
                curRequest.setCookies(map[1]);
            }else if(StringUtils.isEqual("content-length",map[0])){
                curRequest.setContent_length(Long.parseLong(map[1]));
            }else if(StringUtils.isEqual("host",map[0])) {
                curRequest.setHost(map[1].split(":")[0].trim());
            }else if(StringUtils.isEqual("content-type",map[0])) {
                String items[]=map[1].split(";");
                if (items.length<1){
                    logger.debug("invaild line "+ line);
                    throwable=new ParseException(" the contenttype " +
                            "must contain type ");
                    stage=NONE;
                }
                curRequest.setContentType(items[0]);
                if (items.length>1){
                    String boundary[]=items[1].trim().split("=");
                    if (boundary.length!=2||!boundary[0].startsWith("boundary")){
                        throwable=new ParseException(" the invaild " +
                                "boundry ");
                        stage=NONE;
                    }
                    curRequest.setBoundary(boundary[1].trim());
                }

            }
            if (stage==HEAD){
                curRequest.addAttributies(map[0],map[1]);
            }

        }
    }

    private byte[] data=null;
    private int load=0;
    private void parseBody(){

        cur=linend+2;
        int len= (int) curRequest.getContent_length();
        if (data==null){
            data=new byte[ len];
        }
        try{
            int bound=acess.getBound();
            byte[] cache;
            if (bound-cur<(len-load)){
                cache=acess.getRangeBytes(cur,bound-cur);
                System.arraycopy(cache,0,data,load,cache.length);
                load+=(bound-cur);
                acess.mark(bound);
            }else {
                int wrc=(len-load);
                cache=acess.getRangeBytes(cur,(len-load));
                System.arraycopy(cache,0,data,load,cache.length);
                load=len;
                stage=NONE;
                curRequest.setData(data);
                data=null;
                load=0;
                cur+=wrc;
            }
        }catch (Throwable t){
            this.throwable=t;
            stage=NONE;
        }
        logger.debug("has receive body's size "+load);
    }

    private String getHeadLine(){
        String line=getNextLine();
        while (line.equals("")){
            line=getNextLine();
        }
        if (line==null){
            int bound=acess.getBound();
            if (bound>3){
                String tmp=acess.getString(0,Math.min(6,bound));
                if (!headMatch(tmp)){
                    stage=NONE;
                    throwable=new ParseException("the head is invaild");
                }
            }
        }
        return line;
    }

    private void post(HttpRequest request){
        poster.post(request);
    }

    private String getNextLine(){
        try {
            cur=linend+2;
            linend=acess.find(LINE_SEP.getBytes(),cur);
            if (linend==-1){
                return null;
            }else {
                return acess.getString(cur,linend);
            }
        }catch (Throwable throwable){
            this.throwable=throwable;
            stage=NONE;
            return null;
        }
    }

    /**
     *
     * @return if acess statrt with the support http method
     * ture or false
     */
    private boolean headMatch(String str){
        Set<String> methods= Http11Constant.suporrtMethods;
        for (String s:methods){
            if (str.length()>=s.length()){
                if (str.startsWith(s+" ")){
                    return true;
                }
            }
        }

        return false;
    }
}
