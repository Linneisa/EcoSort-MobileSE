package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {

    @SerializedName("id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getId()        { return id; }
    public String getFullName()  { return fullName; }
    public String getPhone()     { return phone; }
    public String getEmail()     { return email; }
    public String getUpdatedAt() { return updatedAt; }
}
