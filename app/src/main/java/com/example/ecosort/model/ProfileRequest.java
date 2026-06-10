package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ProfileRequest {

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

    public ProfileRequest(String id, String fullName, String phone, String email) {
        this.id       = id;
        this.fullName = fullName;
        this.phone    = phone;
        this.email    = email;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.updatedAt = sdf.format(new Date());
    }
}
