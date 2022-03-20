package hasan.mohamed.shehata.myapplication.models;

import java.io.Serializable;

public enum MessageStatus implements Serializable {
    notsent,//default
    sent,
    delivered,
    read,
    deleted
}
