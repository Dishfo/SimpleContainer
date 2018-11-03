package com.sicnu.cs.servlet.container;

import com.sicnu.cs.servlet.basis.ServletPosition;

public interface RegisterListener {
    void onRegister(String url[], ServletPosition position);

}
