package com.cs.sicnu.core.process;

import com.cs.sicnu.core.protocol.Protocol;

/**
 * 投递的中间点既可以投递也可以输出
 *
 * @param <I>
 * @param <O>
 */
public interface ObjPoster<I,O> extends Protocol<O>,Poster<I> {

}
