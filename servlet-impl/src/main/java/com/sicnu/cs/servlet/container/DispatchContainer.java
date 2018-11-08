package com.sicnu.cs.servlet.container;

import com.sicnu.cs.servlet.basis.HttpPair;
import com.sicnu.cs.servlet.basis.map.ServletSearch;

public abstract class DispatchContainer extends RegisterContainer {
    protected abstract void dispatch(HttpPair pair, ServletSearch search);
}
