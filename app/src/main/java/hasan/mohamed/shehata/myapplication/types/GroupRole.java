package hasan.mohamed.shehata.myapplication.types;

import java.io.Serializable;

import kotlin.jvm.Transient;

public enum GroupRole implements Serializable {
    ADMIN,
    ADD_SEND_RECEIVE,
    DELETE_SEND_RECEIVE,
    ADD_DELETE_SEND_RECEIVE,
    SEND_RECEIVE,
    RECEIVE
}
