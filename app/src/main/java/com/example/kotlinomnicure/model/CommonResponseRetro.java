package com.example.kotlinomnicure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonResponseRetro {


    @Expose
    @SerializedName("dischargedCount")
    private int dischargedCount;
    @Expose
    @SerializedName("errorMessage")
    private String errorMessage;
    @Expose
    @SerializedName("errorId")
    private int errorId;
    @Expose
    @SerializedName("status")
    private boolean status;

    public int getDischargedCount() {
        return dischargedCount;
    }

    public void setDischargedCount(int dischargedCount) {
        this.dischargedCount = dischargedCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
