package com.sicnu.cs.servlet.basis;

import java.util.List;

public interface ServletAccess {
    List<String> getUrlPattern(String name);
    String getContexPath();
}
