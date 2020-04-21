package io.hashimati.microstarter.entity;

/**
 * @author Ahmed Al Hashmi @hashimati
 */


import java.util.Date;

@Deprecated
public class MetricsData
{
    private String id;
    private String eventType, country;
    private String ip;
    private Date date;
    private String url;

    public MetricsData() {
        setDate(new Date());
    }

    public MetricsData(String id, String eventType, String country, Date date, String url) {
        this.id = id;
        this.eventType = eventType;
        this.country = country;
        this.date = date;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
