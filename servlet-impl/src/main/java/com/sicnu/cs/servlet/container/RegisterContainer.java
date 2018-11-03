package com.sicnu.cs.servlet.container;

import com.cs.sicnu.core.process.BaseContainer;
import com.cs.sicnu.core.process.Container;
import com.sicnu.cs.servlet.basis.ServletPosition;

public abstract class RegisterContainer extends BaseContainer implements RegisterListener {
    @Override
    public void onRegister(String[] url, ServletPosition position) {
        Container c=getParent();
        if (c instanceof RegisterListener){
            ((RegisterListener) c).onRegister(url,fillPosition(position));
        }
        processRegistered(url,position);
    }

    protected ServletPosition fillPosition(ServletPosition servletPosition){
        return servletPosition;
    }

    protected void processRegistered(String[] url, ServletPosition position){}
}
