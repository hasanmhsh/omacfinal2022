package hasan.mohamed.shehata.myapplication.types;

import java.io.Serializable;

public abstract class SingleObjectReceiver implements Serializable {
    public abstract void receive(Object object);
    public abstract void refreshGroupImage();
}
