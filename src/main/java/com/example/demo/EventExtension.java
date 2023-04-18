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
    public static final String USERID = "userid";
    public static final String PRIORITY = "priority";
    public static final String CORRELATIONID = "correlationid";
    public static final String CONTENTENCODING = "contentencoding";
    public static final String EXPIRATION = "expiration";
    public static final String X_DELAY = "delay";

    public static final String PUBLISHINGID = "publishingid";
    public static final String OFFSET = "offset";
    private static final Set<String> KEY_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(APPID, REPLYTO, USERID, PRIORITY, CORRELATIONID, CONTENTENCODING, EXPIRATION, X_DELAY, PUBLISHINGID, OFFSET)));

    private String appid;
    private String replyto;
    private String userid;
    private Integer priority;
    private String correlationid;
    private String contentencoding;
    private String expiration;
    private Integer delay;

    private Long publishingid;
    private Long offset;

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCorrelationid() {
        return correlationid;
    }

    public void setCorrelationid(String correlationid) {
        this.correlationid = correlationid;
    }

    public String getContentencoding() {
        return contentencoding;
    }

    public void setContentencoding(String contentencoding) {
        this.contentencoding = contentencoding;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Long getPublishingid() {
        return publishingid;
    }

    public void setPublishingid(Long publishingid) {
        this.publishingid = publishingid;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        Object ap = extensions.getExtension(APPID);
        if (ap != null) {
            this.appid = ap.toString();
        }
        Object re = extensions.getExtension(REPLYTO);
        if (re != null) {
            this.replyto = re.toString();
        }
        Object us = extensions.getExtension(USERID);
        if (us != null) {
            this.userid = us.toString();
        }
        Object pr = extensions.getExtension(PRIORITY);
        if (pr != null) {
            this.priority = Integer.valueOf(pr.toString());
        }
        Object co = extensions.getExtension(CORRELATIONID);
        if (co != null) {
            this.correlationid = co.toString();
        }
        Object cd = extensions.getExtension(CONTENTENCODING);
        if (cd != null) {
            this.contentencoding = cd.toString();
        }
        Object ex = extensions.getExtension(EXPIRATION);
        if (ex != null) {
            this.expiration = ex.toString();
        }
        Object de = extensions.getExtension(X_DELAY);
        if (de != null) {
            this.delay = Integer.valueOf(de.toString());
        }
        Object pu = extensions.getExtension(PUBLISHINGID);
        if (pu != null) {
            this.publishingid = Long.valueOf(pu.toString());
        }
        Object of = extensions.getExtension(OFFSET);
        if (of != null) {
            this.offset = Long.valueOf(of.toString());
        }
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
            case APPID:
                return this.appid;
            case REPLYTO:
                return this.replyto;
            case USERID:
                return this.userid;
            case PRIORITY:
                return this.priority;
            case CORRELATIONID:
                return this.correlationid;
            case CONTENTENCODING:
                return this.contentencoding;
            case EXPIRATION:
                return this.expiration;
            case X_DELAY:
                return this.delay;
            case PUBLISHINGID:
                return this.publishingid;
            case OFFSET:
                return this.offset;
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
                ", userid='" + userid + '\'' +
                ", priority=" + priority +
                ", correlationid='" + correlationid + '\'' +
                ", contentencoding='" + contentencoding + '\'' +
                ", expiration='" + expiration + '\'' +
                ", delay=" + delay +
                ", publishingid=" + publishingid +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventExtension that = (EventExtension) o;
        return Objects.equals(getAppid(), that.getAppid()) && Objects.equals(getReplyto(), that.getReplyto()) && Objects.equals(getUserid(), that.getUserid()) && Objects.equals(getPriority(), that.getPriority()) && Objects.equals(getCorrelationid(), that.getCorrelationid()) && Objects.equals(getContentencoding(), that.getContentencoding()) && Objects.equals(getExpiration(), that.getExpiration()) && Objects.equals(getDelay(), that.getDelay()) && Objects.equals(getPublishingid(), that.getPublishingid()) && Objects.equals(getOffset(), that.getOffset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppid(), getReplyto(), getUserid(), getPriority(), getCorrelationid(), getContentencoding(), getExpiration(), getDelay(), getPublishingid(), getOffset());
    }
}


