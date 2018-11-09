package com.sicnu.cs.component;

import com.sicnu.cs.servlet.container.BaseEngine;
import com.sicnu.cs.servlet.container.Engine;
import com.sicnu.cs.servlet.container.SimpleContext;
import com.sicnu.cs.servlet.container.SimpleHost;
import com.sicnu.cs.servlet.init.ClassesTransfer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 * 负责加载对应的配置文件
 * 并且创建web容器
 *
 */
class ConfigInit {
    private Logger logger= LogManager.getLogger(getClass().getName());
    private BaseEngine engine;
    private MessageListener listener;
    private ClassesTransfer transfer;

    ConfigInit() {
        transfer=new ClassesTransfer();
    }

    void config(String path){
        logger.debug("start load config");
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(new File(path));
        } catch (JDOMException | IOException e) {
            logger.error(e.toString());
            return;
        }
        Element tutorials = doc.getRootElement();
        Element eport=tutorials.getChild("port");
        logger.debug("listen port "+eport.getValue());
        listener=new MessageListener(Integer.parseInt(eport.getValue()));
        Element eWeb=tutorials.getChild("Web");

        if (eWeb!=null){
            logger.debug("find engine");
            engine=new BaseEngine();
        }else {
            return;
        }

        List<Element> elements=eWeb.getChildren();
        for (Element e:elements){
            if (e.getName().equals("Host")){
                List<Element> eadr=e.getChildren("address");
                SimpleHost host=new SimpleHost();
                List<InetAddress> ips=parseIp(eadr);
                for (InetAddress address:ips){
                    logger.debug("host address is"+address.toString());
                    host.addAddress(address);
                }
                List<Element> ectxs=e.getChildren("context");
                List<SimpleContext> contexts=parseCtx(ectxs);
                for (SimpleContext context:contexts){
                    host.addChild(context);
                }
                engine.addChild(host);
            }
        }
    }


    private List<SimpleContext> parseCtx(List<Element> elements){
        HashMap<String,SimpleContext> contextHashMap=new HashMap<>();

        for (Element e:elements){
            String contexpath;
            String basePath;
            contexpath=e.getChild("contextPath").getValue();
            basePath=e.getChild("basePath").getValue();
            logger.debug("context "+contexpath+ " "+basePath);
            SimpleContext context=new SimpleContext(basePath,contexpath);
            contextHashMap.put(contexpath,context);
        }

        return new ArrayList<>(contextHashMap.values());
    }


    private List<InetAddress> parseIp(List<Element> elements){
        Set<InetAddress> addresses=new HashSet<>();
        for (Element e:elements){
            String ip=e.getValue();
            if (ip==null){
                continue;
            }
            InetAddress address=null;
            if (ip.equals("ANY_LOCAL")){
                try {
                    address=null;
                    Enumeration<NetworkInterface> interfaceEnumeration=
                            NetworkInterface.getNetworkInterfaces();
                    while (interfaceEnumeration.hasMoreElements()){
                        NetworkInterface ninterface=interfaceEnumeration.nextElement();
                        ninterface.getInterfaceAddresses().forEach(interfaceAddress -> {
                            addresses.add(interfaceAddress.getAddress());
                        });
                    }
                } catch (SocketException e1) {
                    e1.printStackTrace();
                }
            }else {
                try {
                    address = InetAddress.getByName(ip);
                } catch (UnknownHostException e1) {
                    continue;
                }
            }
            if (address!=null){
                addresses.add(address);
            }
        }

        return new ArrayList<>(addresses);
    }

    Engine getEngine() {
        return engine;
    }

    MessageListener getListener(){
        return listener;
    }


}
