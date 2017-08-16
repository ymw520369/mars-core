package org.alan.mars.protostuff;

import org.alan.mars.net.Connect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2017/8/2.
 *
 * @author Alan
 * @since 1.0
 */
public class UserSession<T> {
    /* 属性表*/
    public Map<String, Object> attributeMap = new HashMap<>();
    /* 用户连接*/
    private Connect<T> connect;

    /* 会话ID*/
    private String sessageId;

    public void writeMessage(T t) {
        connect.write(t);
    }

    public UserSession(Connect<T> connect, String sessageId) {
        this.connect = connect;
        this.sessageId = sessageId;
    }

    public Connect<T> getConnect() {
        return connect;
    }

    public void setConnect(Connect<T> connect) {
        this.connect = connect;
    }

    public String getSessageId() {
        return sessageId;
    }

    public void setSessageId(String sessageId) {
        this.sessageId = sessageId;
    }
}
