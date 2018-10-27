package com.cs.sicnu.contextutil;

/**
 *
 * 持有一组 class filter
 * 检查 class 是否满足要求
 *
 */
public interface ClassFilterChain {

    boolean accept(Class cls);
}





