package com.example.demo;

import io.cloudevents.CloudEventExtension;
import io.cloudevents.CloudEventExtensions;
import io.cloudevents.core.extensions.impl.ExtensionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public final class EventExtension implements CloudEventExtension {


    public static final String APPID = "appid";


    public static final String REPLYTO = "replyto";

    private static final Set<String> KEY_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(APPID, REPLYTO)));

    private String appid;
    private String replyto;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getReplyto() {
        return replyto;
    }

    public void setReplyto(String replyto) {
        this.replyto = replyto;
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        Object tp = extensions.getExtension(APPID);
        if (tp != null) {
            this.appid = tp.toString();
        }
        Object ts = extensions.getExtension(REPLYTO);
        if (ts != null) {
            this.replyto = ts.toString();
        }
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
            case APPID:
                return this.appid;
            case REPLYTO:
                return this.replyto;
        }
        throw ExtensionUtils.generateInvalidKeyException(this.getClass(), key);
    }

    @Override
    public Set<String> getKeys() {
        return KEY_SET;
    }

    @Override
    public String toString() {
        return "EventExtension{" +
                "appid='" + appid + '\'' +
                ", replyto='" + replyto + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventExtension that = (EventExtension) o;
        return Objects.equals(getAppid(), that.getAppid()) && Objects.equals(getReplyto(), that.getReplyto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppid(), getReplyto());
    }
}

