package com.example.kotlinomnicure.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SOSResponse implements Serializable {

    @SerializedName("auditId")
    private String auditId;
    @SerializedName("dischargedCount")
    private int dischargedCount;
    @SerializedName("providerList")
    private List<ProviderList> providerList;
    @SerializedName("errorId")
    private int errorId;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("status")
    private boolean status;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

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

    public List<ProviderList> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<ProviderList> providerList) {
        this.providerList = providerList;
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

    public static class ProviderList {
        @SerializedName("remoteProviderType")
        private String remoteProviderType;
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private String id;

        public String getRemoteProviderType() {
            return remoteProviderType;
        }

        public void setRemoteProviderType(String remoteProviderType) {
            this.remoteProviderType = remoteProviderType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
