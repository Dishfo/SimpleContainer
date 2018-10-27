package com.cs.sicnu.contextutil;

import com.cs.sicnu.contextutil.PortContainer.PortFeature;
import com.cs.sicnu.core.process.Bundle;
import com.cs.sicnu.core.process.Port;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;
import java.util.ArrayList;
import java.util.List;

/**
 * for simple for quick for correcte
 * the class be use to
 * store many url-match  /domain/context/servlet
 * <p>
 * 暂时只支持精确匹配
 */
//todo 对域名解析进行进一步完善 查找时 应该具备缓系统
public final class PortMap {

    private static final PortMap instance = new PortMap();

    private List<PortFeature> features=new ArrayList<>();

    //cache
    public static PortMap getInstance() {
        return instance;
    }

    private PortMap() {

    }

    void registerPort(PortFeature feature){
        features.add(feature);
    }

    public static final String DST_PORT="dst_port";
    public static final String SERVLET_MAPPING="servlet_mapping";

    public Bundle findServlet(String domain, String path){
        Bundle bundle=new Bundle();
        Object o;
        Port tmp=null;
        for (PortFeature feature:features){
            if ((o=feature.match(domain,path))!=null
                    &&o instanceof HttpServletMapping){
                bundle.putData(SERVLET_MAPPING,o);
                tmp=feature.getPort();
                if (((HttpServletMapping) o).getMappingMatch()
                        !=MappingMatch.CONTEXT_ROOT
                        &&((HttpServletMapping) o).getMappingMatch()
                    !=MappingMatch.DEFAULT){
                    bundle.putData(DST_PORT,feature.getPort());
                    return bundle;
                }

            }
        }

        if (tmp!=null){
            bundle.putData(DST_PORT,tmp);
        }

        return bundle;
    }
}
