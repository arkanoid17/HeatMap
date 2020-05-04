package com.example.heatmap;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Incidences {
    @SerializedName("incident_id")
    @Expose
    private String incidentId;
    @SerializedName("case_number")
    @Expose
    private String caseNumber;
    @SerializedName("incident_datetime")
    @Expose
    private String incidentDatetime;
    @SerializedName("incident_type_primary")
    @Expose
    private String incidentTypePrimary;
    @SerializedName("incident_description")
    @Expose
    private String incidentDescription;
    @SerializedName("clearance_type")
    @Expose
    private String clearanceType;
    @SerializedName("address_1")
    @Expose
    private String address1;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("location")
    @Expose
    private Locations location;
    @SerializedName("hour_of_day")
    @Expose
    private String hourOfDay;
    @SerializedName("day_of_week")
    @Expose
    private String dayOfWeek;
    @SerializedName("parent_incident_type")
    @Expose
    private String parentIncidentType;

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getIncidentDatetime() {
        return incidentDatetime;
    }

    public void setIncidentDatetime(String incidentDatetime) {
        this.incidentDatetime = incidentDatetime;
    }

    public String getIncidentTypePrimary() {
        return incidentTypePrimary;
    }

    public void setIncidentTypePrimary(String incidentTypePrimary) {
        this.incidentTypePrimary = incidentTypePrimary;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getClearanceType() {
        return clearanceType;
    }

    public void setClearanceType(String clearanceType) {
        this.clearanceType = clearanceType;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Locations getLocation() {
        return location;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }

    public String getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(String hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getParentIncidentType() {
        return parentIncidentType;
    }

    public void setParentIncidentType(String parentIncidentType) {
        this.parentIncidentType = parentIncidentType;
    }

}
