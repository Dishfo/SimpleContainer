package com.sicnu.cs.servlet.basis;

import javax.servlet.FilterChain;

public abstract class FeedBackFilterChain implements FilterChain {

    protected boolean all;

    public boolean isThrough(){
        return all;
    }



}
