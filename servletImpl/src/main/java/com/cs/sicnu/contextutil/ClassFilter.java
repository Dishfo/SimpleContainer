package com.cs.sicnu.contextutil;

/**
 * 对class进行过滤
 *
 */
@FunctionalInterface
public interface ClassFilter {
    boolean isAccept(Class cls);
}
