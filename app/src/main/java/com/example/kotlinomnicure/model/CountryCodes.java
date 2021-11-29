package com.example.kotlinomnicure.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryCodes {

    public CountryCodes() {
    }

    @Expose
    @SerializedName("countryCodeResponseList")
    private List<CountryCodeResponseList> countryCodeResponseList;
    @Expose
    @SerializedName("errorId")
    private int errorId;
    @Expose
    @SerializedName("status")
    private boolean status;

    public List<CountryCodeResponseList> getCountryCodeResponseList() {
        return countryCodeResponseList;
    }

    public void setCountryCodeResponseList(List<CountryCodeResponseList> countryCodeResponseList) {
        this.countryCodeResponseList = countryCodeResponseList;
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

    public static class CountryCodeResponseList {
        @Expose
        @SerializedName("code")
        private String code;
        @Expose
        @SerializedName("name")
        private String name;
        @Expose
        @SerializedName("id")
        private int id;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return String.valueOf(id);
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
