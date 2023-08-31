package org.mx.post.entities;

import java.io.Serializable;

public class Bag<T> implements Serializable {

    public enum bagType{
        LOGIN_CREDENTIALS,
        MAILBOX_DOWNLOAD,
        MAILBOX,
        NOTIFY_RESULT,
        DELETE_MAIL,
        MAIL_OUT
    }
    public enum resultCode{
        SUCCESS,
        FAILED
    }
    T payload;
    bagType type;
    resultCode resultCode;

    public Bag(T payload, bagType type, Bag.resultCode resultCode) {
        this.payload = payload;
        this.type = type;
        this.resultCode = resultCode;
    }

    public bagType getType() {
        return type;
    }

    public void setType(bagType type) {
        this.type = type;
    }

    public Bag.resultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(Bag.resultCode resultCode) {
        this.resultCode = resultCode;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
