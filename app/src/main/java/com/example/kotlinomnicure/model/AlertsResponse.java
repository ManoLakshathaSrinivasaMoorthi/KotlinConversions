package com.example.kotlinomnicure.model;

import com.example.dailytasksamplepoc.kotlinomnicure.endpoints.SystemAlerts;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AlertsResponse {

    @Expose
    @SerializedName("systemAlertList")
    private List<SystemAlerts> systemAlertList;
    @Expose
    @SerializedName("errorId")
    private int errorId;
    @Expose
    @SerializedName("errorMessage")
    private String errorMessage;
    @Expose
    @SerializedName("status")
    private boolean status;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isStatus() {
        return status;
    }

    public List<SystemAlerts> getSystemAlertList() {
        return systemAlertList;
    }

    public void setSystemAlertList(List<SystemAlerts> systemAlertList) {
        this.systemAlertList = systemAlertList;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
