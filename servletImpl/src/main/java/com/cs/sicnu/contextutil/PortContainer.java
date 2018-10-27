package com.cs.sicnu.contextutil;

import com.cs.sicnu.core.process.BaseContainer;
import com.cs.sicnu.core.process.Container;
import com.cs.sicnu.core.process.Port;
import com.cs.sicnu.core.utils.StringUtils;
import com.cs.sicnu.http.HttpServletMappingImpl;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class PortContainer extends BaseContainer implements PortRegister, Port {

    protected PortContainer parent;
    /**
     * 把当前容器注册到port map中
     */
    @Override
    public void register(Feature feature) {
        if (wisHandon()) {
            parent.register(getFeature(feature));
        } else {
            registerFeature(getFeature(feature));
        }
    }

    public void setParent(Container parent) {
        if (parent instanceof PortContainer)
            this.parent = (PortContainer) parent;
        else
            super.setParent(parent);
    }

    @Override
    public PortContainer getParent() {
        return parent;
    }

    protected abstract void registerFeature(Feature feature);

    protected abstract Feature getFeature(Feature feature);

    protected abstract boolean wisHandon();

    protected static     class ServletFeature implements Feature {
        String servletName;
        UrlPattern[] urlpatterns;
        String[] origPatterns;
        HttpServletMapping defaultMapping;

        public ServletFeature(String servletName, String[] urlpatterns) {
            this.servletName = servletName;
            origPatterns=urlpatterns;
            defaultMapping = new HttpServletMappingImpl("", "/",
                    "", MappingMatch.DEFAULT);
            this.urlpatterns = new UrlPattern[urlpatterns.length];
            for (int i = 0, j = 0; i < urlpatterns.length; i++) {
                if (urlpatterns[i] != null)
                    this.urlpatterns[j++] = new UrlPattern(urlpatterns[i]);
            }
        }

        @Override
        public Object match(String... args) {
            String path = args[0];
            HttpServletMapping res;
            for (UrlPattern pattern:urlpatterns){
                if ((res=matchInteral(pattern,path))!=null){
                    return res;
                }
            }
            return defaultMapping;
        }

        private HttpServletMapping matchInteral(UrlPattern pattern,String path){
            switch (pattern.type){
                case PTYPE_EXCAT:
                    if (path.equals(pattern.pattern)){
                        return new HttpServletMappingImpl(path,
                                pattern.pattern,servletName,MappingMatch.EXACT);
                    }
                    break;
                case PTYPE_EXTEN:
                    if (Pattern.compile(pattern.pattern).matcher(path).matches()){
                        return new HttpServletMappingImpl("",pattern.pattern,
                                servletName,MappingMatch.EXTENSION);
                    }
                    break;
                case PTYPE_PATH:
                    if (Pattern.compile(pattern.pattern).matcher(path).matches()){
                        return new HttpServletMappingImpl("",pattern.pattern,
                                servletName,MappingMatch.PATH);
                    }
                    break;
                default:
                    break;
            }
            return null;
        }
    }

    private static final int PTYPE_EXCAT = 0x1;
    private static final int PTYPE_EXTEN = 0x2;
    private static final int PTYPE_PATH = 0x3;

    private static final Pattern EXT_SUF = Pattern.compile("/\\*\\.[A-Za-z]+$");

    private static class UrlPattern {
        String pattern;
        int type;

        UrlPattern(String pattern) {
            if (pattern.equals("/") ||
                    pattern.equals("") ||
                    pattern.equals("*")) {
                throw new IllegalArgumentException("hahahahahahahhahaha");
            }
            this.pattern = pattern;
            if (pattern.endsWith("/*")) {
                type = PTYPE_PATH;
            } else if (EXT_SUF.matcher(pattern).matches()) {
                type = PTYPE_EXTEN;
            } else {
                type = PTYPE_EXCAT;
            }
            patternCorrection();
        }

        private void patternCorrection() {
            switch (type) {
                case PTYPE_EXCAT:
                    break;
                case PTYPE_EXTEN:
                    pattern="^"+ StringUtils.convertToRegex(pattern)+"$";
                    break;
                case PTYPE_PATH:
                    pattern="^"+ StringUtils.convertToRegex(pattern)+"$";
                    break;
                default:
                    break;
            }
        }
    }

    protected static class ContextFeature implements Feature {
        String context;
        Port port;
        private ServletFeature feature;
        private HttpServletMapping contextRoot;

        public ContextFeature(String context,
                              ServletFeature feature,
                              Port port) {
            this.context = context;
            this.feature = feature;
            this.port = port;
            contextRoot = new HttpServletMappingImpl("", "",
                    "", MappingMatch.CONTEXT_ROOT);
        }

        @Override
        public Object match(String... args) {
            String path = args[0];
            if (path.startsWith(context)) {
                String spath = path.substring(context.length());
                return feature.match(spath);
            } else if (path.equals("")) {
                return contextRoot;
            }
            return null;
        }
    }

    protected static class DomainFeature implements Feature {

        InetAddress address;
        private ContextFeature feature;

        DomainFeature(String domain, ContextFeature feature) {
            try {
                address = InetAddress.getByName(domain);
            } catch (UnknownHostException ignored) {
            }
            Objects.requireNonNull(address);
            this.feature = feature;
        }

        /**
         * @param args host path
         * @return true match
         */
        @Override
        public Object match(String... args) {
            if (address.getHostAddress().equals(args[0]) ||
                    address.getHostName().equals(args[0])) {
                return feature.match(args[1]);
            }
            return null;
        }
    }

    protected static class PortFeature implements Feature {
        private Port port;
        private DomainFeature domainFeature;

        PortFeature(DomainFeature domainFeature) {
            this.port = domainFeature.feature.port;
            this.domainFeature = domainFeature;
        }

        /**
         * 判断一个 domain/path 能否找到一个匹配的servlet
         *
         * @param args doamin and path
         * @return true match
         */
        @Override
        public Object match(String... args) {
            return domainFeature.match(args[0], args[1]);
        }

        Port getPort() {
            return port;
        }
    }
}
