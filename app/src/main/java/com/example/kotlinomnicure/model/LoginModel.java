package com.example.kotlinomnicure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {

    public LoginModel() {
    }

    @Expose
    @SerializedName("agoraAppCertificate")
    private String agoraAppCertificate;
    @Expose
    @SerializedName("agoraAppId")
    private String agoraAppId;
    @Expose
    @SerializedName("aesEncryptionKey")
    private String aesEncryptionKey;
    @Expose
    @SerializedName("encryptionKey")
    private String encryptionKey;
    @Expose
    @SerializedName("refreshToken")
    private String refreshToken;
    @Expose
    @SerializedName("idToken")
    private String idToken;
    @Expose
    @SerializedName("tutorial_url")
    private String tutorial_url;
    @Expose
    @SerializedName("feedbackForm")
    private String feedbackForm;
    @Expose
    @SerializedName("dischargedCount")
    private int dischargedCount;
    @Expose
    @SerializedName("provider")
    private Provider provider;
    @Expose
    @SerializedName("statusMessage")
    private String statusMessage;
    @Expose
    @SerializedName("errorId")
    private int errorId;
    @Expose
    @SerializedName("status")
    private boolean status;
    @Expose
    @SerializedName("errorMessage;")
    private String errorMessage;

    public boolean isStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAgoraAppCertificate() {
        return agoraAppCertificate;
    }

    public void setAgoraAppCertificate(String agoraAppCertificate) {
        this.agoraAppCertificate = agoraAppCertificate;
    }

    public String getAgoraAppId() {
        return agoraAppId;
    }

    public void setAgoraAppId(String agoraAppId) {
        this.agoraAppId = agoraAppId;
    }

    public String getAesEncryptionKey() {
        return aesEncryptionKey;
    }

    public void setAesEncryptionKey(String aesEncryptionKey) {
        this.aesEncryptionKey = aesEncryptionKey;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getTutorial_url() {
        return tutorial_url;
    }

    public void setTutorial_url(String tutorial_url) {
        this.tutorial_url = tutorial_url;
    }

    public String getFeedbackForm() {
        return feedbackForm;
    }

    public void setFeedbackForm(String feedbackForm) {
        this.feedbackForm = feedbackForm;
    }

    public int getDischargedCount() {
        return dischargedCount;
    }

    public void setDischargedCount(int dischargedCount) {
        this.dischargedCount = dischargedCount;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
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

    public static class Provider {
        @Expose
        @SerializedName("status")
        private String status;
        @Expose
        @SerializedName("userId")
        private String userId;
        @Expose
        @SerializedName("remoteProviderType")
        private String remoteProviderType;
        @Expose
        @SerializedName("token")
        private String token;
        @Expose
        @SerializedName("hospital")
        private String hospital;
        @Expose
        @SerializedName("phone")
        private String phone;
        @Expose
        @SerializedName("role")
        private String role;
        @Expose
        @SerializedName("email")
        private String email;
        @Expose
        @SerializedName("lname")
        private String lname;
        @Expose
        @SerializedName("fname")
        private String fname;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("id")
        private String id;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRemoteProviderType() {
            return remoteProviderType;
        }

        public void setRemoteProviderType(String remoteProviderType) {
            this.remoteProviderType = remoteProviderType;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
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
