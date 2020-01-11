package com.xml.handler;

public class Data {

    private String sender;
    private String recId;
    private String recOrgNr;
    private String recName;
    private String recStreet;
    private String recZip;
    private String recCity;

    public Data(String sender, String recId, String recOrgNr,
                String recName, String recStreet, String recZip, String recCity) {
        this.sender = removeBOM(sender);
        this.recId = recId;
        this.recOrgNr = recOrgNr;
        this.recName = recName;
        this.recStreet = recStreet;
        this.recZip = recZip;
        this.recCity = recCity;
    }

    public String getSender() {
        return sender;
    }

    public String getRecId() {
        return recId;
    }

    public String getRecOrgNr() {
        return recOrgNr;
    }

    public String getRecName() {
        return recName;
    }

    public String getRecStreet() {
        return recStreet;
    }

    public String getRecZip() {
        return recZip;
    }

    public String getRecCity() {
        return recCity;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    private String removeBOM(String s) {
        return s.startsWith("\uFEFF") ? s.substring(1) : s;
    }
}