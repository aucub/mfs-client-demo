package com.example.demo;

import io.cloudevents.CloudEventExtension;
import io.cloudevents.CloudEventExtensions;
import io.cloudevents.core.extensions.impl.ExtensionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public final class EventExtension implements CloudEventExtension {
    public static final String APPID = "appid";
    public static final String PRIORITY = "priority";
    public static final String CONTENTENCODING = "contentencoding";
    public static final String EXPIRATION = "expiration";
    public static final String X_DELAY = "delay";
    public static final String PUBLISHINGID = "publishingid";
    public static final String OFFSET = "offset";
    private static final Set<String> KEY_SET = Set.of(APPID, PRIORITY, CONTENTENCODING, EXPIRATION, X_DELAY, PUBLISHINGID, OFFSET);
    private String appid;
    private String contentencoding;
    private Long publishingid;
    private Integer priority;
    private Integer delay;
    private Long expiration;
    private Long offset;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getContentencoding() {
        return contentencoding;
    }

    public void setContentencoding(String contentencoding) {
        this.contentencoding = contentencoding;
    }

    public Long getPublishingid() {
        return publishingid;
    }

    public void setPublishingid(Long publishingid) {
        this.publishingid = publishingid;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }


    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        Object ap = extensions.getExtension(APPID);
        if (ap != null) {
            this.appid = ap.toString();
        }
        Object pr = extensions.getExtension(PRIORITY);
        if (pr != null) {
            this.priority = Integer.valueOf(pr.toString());
        }
        Object cd = extensions.getExtension(CONTENTENCODING);
        if (cd != null) {
            this.contentencoding = cd.toString();
        }
        Object et = extensions.getExtension(EXPIRATION);
        if (et != null) {
            this.expiration = Long.valueOf(et.toString());
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
            case APPID -> {
                return this.appid;
            }
            case PRIORITY -> {
                return this.priority;
            }
            case CONTENTENCODING -> {
                return this.contentencoding;
            }
            case EXPIRATION -> {
                return this.expiration;
            }
            case X_DELAY -> {
                return this.delay;
            }
            case PUBLISHINGID -> {
                return this.publishingid;
            }
            case OFFSET -> {
                return this.offset;
            }
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
                ", appid='" + appid + '\'' +
                ", contentencoding='" + contentencoding + '\'' +
                ", publishingid=" + publishingid +
                ", priority=" + priority +
                ", delay=" + delay +
                ", expiration=" + expiration +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventExtension that = (EventExtension) o;
        return Objects.equals(getAppid(), that.getAppid()) && Objects.equals(getContentencoding(), that.getContentencoding()) && Objects.equals(getPublishingid(), that.getPublishingid()) && Objects.equals(getPriority(), that.getPriority()) && Objects.equals(getDelay(), that.getDelay()) && Objects.equals(getExpiration(), that.getExpiration()) && Objects.equals(getOffset(), that.getOffset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAppid(), getContentencoding(), getPublishingid(), getPriority(), getDelay(), getExpiration(), getOffset());
    }
}


