/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlinomnicure.model;

import static java.util.Objects.hash;

import android.os.Build;

import androidx.annotation.RequiresApi;


import com.example.kotlinomnicure.utils.Constants;

import java.io.Serializable;
import java.util.Objects;

public class ConsultProvider implements Serializable {

    private Long id;
    private Long patientsId;
    private Long patientId;
    private String text;
    private String name;
    private String fname;
    private String lname;
    private int unread;
    private Long time;
    private Long inviteTime;
    private String msgName;
    private String wardName;
    private String recordNumber;
    private String teamName;

    private int memberCount;

    private Long bdProviderId;
    private String bdProviderName;

    private Long rdProviderId;
    private String rdProviderName;


    private String gender;


    private String email;

    private Long dob;

    private String address;

    private String phone;

    private String countryCode;

    private String hospital;

    private Long hospitalId;
    private String picUrl;

    private Constants.PatientStatus status;

    private Long joiningTime;

    private Long syncTime;

    private Long dischargeTime;
    private String bed;
    private String note;
    private Constants.PatientCondition patientCondition;
    private Boolean oxygenSupplement;
    private Boolean urgent;
    private Boolean resetAcuityFlag;

    //Metrics
    private String docBoxPatientId;
    private String docBoxManagerId;
    private Long timeHeartRate;
    private Long timeSpO2;
    private Double arterialBloodPressureSystolic;
    private Double heartRate;
    private Long timeRespiratoryRate;
    //    private Double sp02;
    private Double respiratoryRate;
    private Double arterialBloodPressureDiastolic;
    private Long timeArterialBloodPressureDiastolic;
    private Long timeArterialBloodPressureSystolic;
    //    private Double fiO2;
    private Long timeFiO2;
    private Integer dobDay;
    private String dobMonth;
    private String completed_by;
    private Integer dobYear;
    private Constants.AcuityLevel score;
    private Double fio2;
    private Double spO2;
    private Double temperature;
    private int unreadCount;

    public ConsultProvider() {
    }

    public ConsultProvider(Long id, Long patientId, String text, String name, int unread, Long time, Constants.PatientStatus status) {
        this.id = id;
        this.patientsId = patientId;
        this.text = text;
        this.name = name;
        this.unread = unread;
        this.time = time;
        this.status = status;
    }

    public ConsultProvider(Long patientId, String text, String name, int unread, Long time, Constants.PatientStatus status) {
        this.id = id;
        this.patientsId = patientId;
        this.text = text;
        this.name = name;
        this.unread = unread;
        this.time = time;
        this.status = status;
    }

    public Long getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(Long inviteTime) {
        this.inviteTime = inviteTime;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getCompleted_by() {
        return completed_by;
    }

    public void setCompleted_by(String completed_by) {
        this.completed_by = completed_by;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Long syncTime) {
        this.syncTime = syncTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public Long getPatientsId() {
        return patientsId;
    }

    public void setPatientsId(Long patientsId) {
        this.patientsId = patientsId;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public Long getBdProviderId() {
        return bdProviderId;
    }

    public void setBdProviderId(Long bdProviderId) {
        this.bdProviderId = bdProviderId;
    }

    public String getBdProviderName() {
        return bdProviderName;
    }

    public void setBdProviderName(String bdProviderName) {
        this.bdProviderName = bdProviderName;
    }

    public Long getRdProviderId() {
        return rdProviderId;
    }

    public void setRdProviderId(Long rdProviderId) {
        this.rdProviderId = rdProviderId;
    }

    public String getRdProviderName() {
        return rdProviderName;
    }

    public void setRdProviderName(String rdProviderName) {
        this.rdProviderName = rdProviderName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getDob() {
        return dob;
    }

    public void setDob(Long dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Constants.PatientStatus getStatus() {
        return status;
    }

    public void setStatus(Constants.PatientStatus status) {
        this.status = status;
    }

    public Long getJoiningTime() {
        return joiningTime;
    }

    public void setJoiningTime(Long joiningTime) {
        this.joiningTime = joiningTime;
    }

    public Long getDischargeTime() {
        return dischargeTime;
    }

    public void setDischargeTime(Long dischargeTime) {
        this.dischargeTime = dischargeTime;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDocBoxPatientId() {
        return docBoxPatientId;
    }

    public void setDocBoxPatientId(String docBoxPatientId) {
        this.docBoxPatientId = docBoxPatientId;
    }

    public String getDocBoxManagerId() {
        return docBoxManagerId;
    }

    public void setDocBoxManagerId(String docBoxManagerId) {
        this.docBoxManagerId = docBoxManagerId;
    }

    public Long getTimeHeartRate() {
        return timeHeartRate;
    }

    public void setTimeHeartRate(Long timeHeartRate) {
        this.timeHeartRate = timeHeartRate;
    }

    public Long getTimeSpO2() {
        return timeSpO2;
    }

    public void setTimeSpO2(Long timeSpO2) {
        this.timeSpO2 = timeSpO2;
    }

    public Long getTimeRespiratoryRate() {
        return timeRespiratoryRate;
    }

    public void setTimeRespiratoryRate(Long timeRespiratoryRate) {
        this.timeRespiratoryRate = timeRespiratoryRate;
    }

    public Double getArterialBloodPressureSystolic() {
        return arterialBloodPressureSystolic;
    }

   /* public Double getSp02() {
        return sp02;
    }

    public void setSp02(Double spO2) {
        this.sp02 = spO2;
    }*/

    public void setArterialBloodPressureSystolic(Double arterialBloodPressureSystolic) {
        this.arterialBloodPressureSystolic = arterialBloodPressureSystolic;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public Double getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Double respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public Double getArterialBloodPressureDiastolic() {
        return arterialBloodPressureDiastolic;
    }

    public void setArterialBloodPressureDiastolic(Double arterialBloodPressureDiastolic) {
        this.arterialBloodPressureDiastolic = arterialBloodPressureDiastolic;
    }

    public Double getFio2() {
        return fio2;
    }

    public void setFio2(Double fiO2) {
        this.fio2 = fiO2;
    }

    public Double getSpO2() {
        return spO2;
    }

    public void setSpO2(Double spO2) {
        this.spO2 = spO2;
    }

    public Long getTimeArterialBloodPressureDiastolic() {
        return timeArterialBloodPressureDiastolic;
    }

    public void setTimeArterialBloodPressureDiastolic(Long timeArterialBloodPressureDiastolic) {
        this.timeArterialBloodPressureDiastolic = timeArterialBloodPressureDiastolic;
    }

    public Long getTimeArterialBloodPressureSystolic() {
        return timeArterialBloodPressureSystolic;
    }

    public void setTimeArterialBloodPressureSystolic(Long timeArterialBloodPressureSystolic) {
        this.timeArterialBloodPressureSystolic = timeArterialBloodPressureSystolic;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }


    public Long getTimeFiO2() {
        return timeFiO2;
    }

    public void setTimeFiO2(Long timeFiO2) {
        this.timeFiO2 = timeFiO2;
    }

    public Constants.PatientCondition getPatientCondition() {
        return patientCondition;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setPatientCondition(Constants.PatientCondition patientCondition) {
        this.patientCondition = patientCondition;
    }

    public Boolean getOxygenSupplement() {
        return oxygenSupplement;
    }

    public void setOxygenSupplement(Boolean oxygenSupplement) {
        this.oxygenSupplement = oxygenSupplement;
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Boolean getResetAcuityFlag() {
        return resetAcuityFlag;
    }

    public void setResetAcuityFlag(Boolean resetAcuityFlag) {
        this.resetAcuityFlag = resetAcuityFlag;
    }

    public Integer getDobDay() {
        return dobDay;
    }

    public void setDobDay(Integer dobDay) {
        this.dobDay = dobDay;
    }

    public String getDobMonth() {
        return dobMonth;
    }

    public void setDobMonth(String dobMonth) {
        this.dobMonth = dobMonth;
    }

    public Integer getDobYear() {
        return dobYear;
    }

    public void setDobYear(Integer dobYear) {
        this.dobYear = dobYear;
    }


    public Constants.AcuityLevel getScore() {
        return score;
    }

    public void setScore(Constants.AcuityLevel score) {
        this.score = score;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultProvider consultProvider = (ConsultProvider) o;
        return unread == consultProvider.unread &&
                Objects.equals(time, consultProvider.time) &&
                Objects.equals(id, consultProvider.id) &&
                Objects.equals(text, consultProvider.text) &&
                Objects.equals(msgName, consultProvider.msgName) &&
                Objects.equals(bdProviderId, consultProvider.bdProviderId) &&
                Objects.equals(bdProviderName, consultProvider.bdProviderName) &&
                Objects.equals(rdProviderId, consultProvider.rdProviderId) &&
                Objects.equals(rdProviderName, consultProvider.rdProviderName) &&
                Objects.equals(picUrl, consultProvider.picUrl) &&
                status == consultProvider.status &&
                Objects.equals(joiningTime, consultProvider.joiningTime) &&
                Objects.equals(dischargeTime, consultProvider.dischargeTime) &&
                Objects.equals(note, consultProvider.note) &&
                patientCondition == consultProvider.patientCondition &&
                Objects.equals(oxygenSupplement, consultProvider.oxygenSupplement) &&
                Objects.equals(resetAcuityFlag, consultProvider.resetAcuityFlag) &&
                Objects.equals(urgent, consultProvider.urgent);
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return hash(id, patientsId, text, unread, time, msgName, bdProviderId, bdProviderName, rdProviderId, rdProviderName, picUrl, status, joiningTime, dischargeTime, note, patientCondition, oxygenSupplement, urgent, resetAcuityFlag);
        }
        return id.intValue();
    }

    @Override
    public String toString() {
        return "ConsultProvider{" +
                "Id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", Status=" + status +
                ", unread=" + unread +
                ", score=" + score +
                '}';
    }
}
