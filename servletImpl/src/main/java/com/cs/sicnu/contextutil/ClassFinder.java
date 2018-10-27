package com.cs.sicnu.contextutil;

import java.util.List;

public interface ClassFinder {
    List<Class> find();

    class ClassInfo {
        private String className;
        private String path;

        public ClassInfo(String className, String path) {
            this.className = className;
            this.path = path;
        }


        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
