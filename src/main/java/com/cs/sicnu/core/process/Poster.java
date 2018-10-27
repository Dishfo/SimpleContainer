package com.cs.sicnu.core.process;

/**
 * 在当前线程中把实例投送给另一个类
 * 用于进行多线程的并发时使用
 * @param <T>
 */
public interface Poster<T> {
    void post(T t);
}
