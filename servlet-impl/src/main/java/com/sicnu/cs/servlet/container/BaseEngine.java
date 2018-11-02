package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.BaseContainer;
import com.sicnu.cs.http.HttpConnection;
import com.sicnu.cs.http.HttpRequest;
import com.sicnu.cs.http.HttpResponse;
import com.sicnu.cs.servlet.http.HttpPair;

public class BaseEngine extends BaseContainer implements Engine {


    @Override
    protected void initInteral() {
        
    }

    @Override
    public void handleRequset(HttpConnection connection,
                              HttpRequest request,
                              HttpResponse response) {
        HttpPair pair=
                new HttpPair(connection,response,request);
    }
}
