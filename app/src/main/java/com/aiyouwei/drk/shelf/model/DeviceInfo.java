package com.aiyouwei.drk.shelf.model;


import com.alibaba.fastjson.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;

public class DeviceInfo {

    private String fullName;
    private String sessionId;
    private String itemName;
    private String partNumber;
    private String room;
    private String shelf;
    private String binName;
    private Integer personId;
    private Integer binId;
    private Integer calWeightQty;
    private Integer preQty;
    private Integer qty;
    private Integer sessionQty;
    private Integer lastPickQty;
    private Date  lastUpdated;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getBinName() {
        return binName;
    }

    public void setBinName(String binName) {
        this.binName = binName;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getBinId() {
        return binId;
    }

    public void setBinId(Integer binId) {
        this.binId = binId;
    }

    public Integer getPreQty() {
        return preQty;
    }

    public void setPreQty(Integer preQty) {
        this.preQty = preQty;
    }

    public Integer getCalWeightQty() {
        return calWeightQty;
    }

    public void setCalWeightQty(Integer calWeightQty) {
        this.calWeightQty = calWeightQty;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getSessionQty() {
        return sessionQty;
    }

    public void setSessionQty(Integer sessionQty) {
        this.sessionQty = sessionQty;
    }

    public Integer getLastPickQty() {
        return lastPickQty;
    }

    public void setLastPickQty(Integer lastPickQty) {
        this.lastPickQty = lastPickQty;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    @Override
    public String toString() {
        return "DeviceInfo{" +
                "fullName='" + fullName + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", room='" + room + '\'' +
                ", shelf='" + shelf + '\'' +
                ", binName='" + binName + '\'' +
                ", personId=" + personId +
                ", binId=" + binId +
                ", calWeightQty=" + calWeightQty +
                ", qty=" + qty +
                ", sessionQty=" + sessionQty +
                ", lastPickQty=" + lastPickQty +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
