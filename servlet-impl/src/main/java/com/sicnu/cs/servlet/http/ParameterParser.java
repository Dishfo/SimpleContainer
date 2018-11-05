package com.sicnu.cs.servlet.http;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ParameterParser {
    Map<String,String> parse(HttpServletRequest request);
}
