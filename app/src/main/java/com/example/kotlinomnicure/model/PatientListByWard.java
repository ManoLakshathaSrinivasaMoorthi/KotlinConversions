package com.example.kotlinomnicure.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public  class PatientListByWard implements Serializable {

    @SerializedName("totalPatientCount")
    private int totalPatientCount;
    
    @SerializedName("wardPatientList")
    private List<WardPatientList> wardPatientList;
    
    @SerializedName("hospitalId")
    private String hospitalId;
    
    @SerializedName("errorMessage")
    private int errorMessage;
    
    @SerializedName("errorId")
    private int errorId;
    
    @SerializedName("status")
    private boolean status;

    public int getTotalPatientCount() {
        return totalPatientCount;
    }

    public void setTotalPatientCount(int totalPatientCount) {
        this.totalPatientCount = totalPatientCount;
    }

    public List<WardPatientList> getWardPatientList() {
        return wardPatientList;
    }

    public void setWardPatientList(List<WardPatientList> wardPatientList) {
        this.wardPatientList = wardPatientList;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(int errorMessage) {
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

    public static class WardPatientList {
        
        @SerializedName("count")
        private int count;
        
        @SerializedName("patientList")
        private List<PatientList> patientList;
        
        @SerializedName("wardName")
        private String wardName;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<PatientList> getPatientList() {
            return patientList;
        }

        public void setPatientList(List<PatientList> patientList) {
            this.patientList = patientList;
        }

        public String getWardName() {
            return wardName;
        }

        public void setWardName(String wardName) {
            this.wardName = wardName;
        }
    }

    public static class PatientList {
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("time")
        private String time;
        
        @SerializedName("completed_by")
        private String completed_by;
        
        @SerializedName("dateOfBirth")
        private String dateOfBirth;
        
        @SerializedName("teamName")
        private String teamName;
        
        @SerializedName("teamId")
        private String teamId;
        
        @SerializedName("covidPositive")
        private String covidPositive;
        
        @SerializedName("consultId")
        private String consultId;
        
        @SerializedName("urgent")
        private boolean urgent;
        
        @SerializedName("patientCondition")
        private String patientCondition;
        
        @SerializedName("oxygenSupplement")
        private boolean oxygenSupplement;
        
        @SerializedName("gcsScore")
        private int gcsScore;
        
        @SerializedName("qSofaScore")
        private int qSofaScore;
        
        @SerializedName("score")
        private String score;
        
        @SerializedName("lastMessageTime")
        private String lastMessageTime;
        
        @SerializedName("inviteTime")
        private String inviteTime;
        
        @SerializedName("acceptTime")
        private String acceptTime;
        
        @SerializedName("joiningTime")
        private String joiningTime;
        
        @SerializedName("hospitalId")
        private String hospitalId;
        
        @SerializedName("hospital")
        private String hospital;
        
        @SerializedName("dob")
        private String dob;
        
        @SerializedName("gender")
        private String gender;
        
        @SerializedName("rdProviderName")
        private String rdProviderName;
        
        @SerializedName("rdProviderId")
        private String rdProviderId;
        
        @SerializedName("bdProviderName")
        private String bdProviderName;
        
        @SerializedName("bdProviderId")
        private String bdProviderId;
        
        @SerializedName("note")
        private String note;
        
        @SerializedName("lname")
        private String lname;
        
        @SerializedName("fname")
        private String fname;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("id")
        private String id;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCompleted_by() {
            return completed_by;
        }

        public void setCompleted_by(String completed_by) {
            this.completed_by = completed_by;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getCovidPositive() {
            return covidPositive;
        }

        public void setCovidPositive(String covidPositive) {
            this.covidPositive = covidPositive;
        }

        public String getConsultId() {
            return consultId;
        }

        public void setConsultId(String consultId) {
            this.consultId = consultId;
        }

        public boolean getUrgent() {
            return urgent;
        }

        public void setUrgent(boolean urgent) {
            this.urgent = urgent;
        }

        public String getPatientCondition() {
            return patientCondition;
        }

        public void setPatientCondition(String patientCondition) {
            this.patientCondition = patientCondition;
        }

        public boolean getOxygenSupplement() {
            return oxygenSupplement;
        }

        public void setOxygenSupplement(boolean oxygenSupplement) {
            this.oxygenSupplement = oxygenSupplement;
        }

        public int getGcsScore() {
            return gcsScore;
        }

        public void setGcsScore(int gcsScore) {
            this.gcsScore = gcsScore;
        }

        public int getQSofaScore() {
            return qSofaScore;
        }

        public void setQSofaScore(int qSofaScore) {
            this.qSofaScore = qSofaScore;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getLastMessageTime() {
            return lastMessageTime;
        }

        public void setLastMessageTime(String lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
        }

        public String getInviteTime() {
            return inviteTime;
        }

        public void setInviteTime(String inviteTime) {
            this.inviteTime = inviteTime;
        }

        public String getAcceptTime() {
            return acceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            this.acceptTime = acceptTime;
        }

        public String getJoiningTime() {
            return joiningTime;
        }

        public void setJoiningTime(String joiningTime) {
            this.joiningTime = joiningTime;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(String hospitalId) {
            this.hospitalId = hospitalId;
        }

        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getRdProviderName() {
            return rdProviderName;
        }

        public void setRdProviderName(String rdProviderName) {
            this.rdProviderName = rdProviderName;
        }

        public String getRdProviderId() {
            return rdProviderId;
        }

        public void setRdProviderId(String rdProviderId) {
            this.rdProviderId = rdProviderId;
        }

        public String getBdProviderName() {
            return bdProviderName;
        }

        public void setBdProviderName(String bdProviderName) {
            this.bdProviderName = bdProviderName;
        }

        public String getBdProviderId() {
            return bdProviderId;
        }

        public void setBdProviderId(String bdProviderId) {
            this.bdProviderId = bdProviderId;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
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
