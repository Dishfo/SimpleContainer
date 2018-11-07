package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.servlet.basis.map.NodeAcessImpl;
import com.sicnu.cs.servlet.basis.map.RootNode;
import com.sicnu.cs.servlet.basis.map.ServletSearch;
import org.junit.Test;

import java.net.*;

public class NodeOperation {

    @Test
    public void test(){
        try {
            InetAddress address1=InetAddress.getByName("222.196.186.128");
            System.out.println(address1+address1.getHostName());
            InetAddress address2=InetAddress.getByName("localhost");
            System.out.println(address2);

            System.out.println(address1.equals(address2));

            RootNode rootNode=new RootNode();
            NodeAcessImpl acess=new NodeAcessImpl();

            ServletPosition position=new ServletPosition();
            position.setContextPath("/test");
            position.setServletName("aserver");
            position.addHost(address1);

            ServletPosition position1=new ServletPosition();
            position1.setContextPath("/test");
            position1.setServletName("bserver");
            position1.addHost(address1);

            ServletPosition position2=new ServletPosition();
            position2.setContextPath("/test");
            position2.setServletName("cserver");
            position2.addHost(address2);


            acess.add(rootNode,position,"/testa");
            acess.add(rootNode,position1,"/testb");
            acess.add(rootNode,position2,"/testc");

            String url="http://127.0.0.1/test/testc";
            ServletSearch search=acess.find(rootNode, URI.create(url).toURL());

            System.out.println("end");
        } catch (UnknownHostException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
