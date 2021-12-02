package com.example.kotlinomnicure.apiRetrofit.RequestBodys;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistrationVerifyRequestBody {

    //changed the body keys as per backend mentioned body

    @Expose
    @SerializedName("isEmailOtpVerified")
    private String isEmailOtpVerified;
    @Expose
    @SerializedName("fcmkey")
    private String fcmkey;
    @Expose
    @SerializedName("osType")
    private String osType;
    @Expose
    @SerializedName("channel")
    private String channel;
    @Expose
    @SerializedName("otp")
    private String otp;
    @Expose
    @SerializedName("id")
    private String id;

    public String getIsEmailOtpVerified() {
        return isEmailOtpVerified;
    }

    public void setIsEmailOtpVerified(String isEmailOtpVerified) {
        this.isEmailOtpVerified = isEmailOtpVerified;
    }

    public String getFcmkey() {
        return fcmkey;
    }

    public void setFcmkey(String fcmkey) {
        this.fcmkey = fcmkey;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

