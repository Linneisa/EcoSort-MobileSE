package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class AuthUser {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("email_confirmed_at")
    private String emailConfirmedAt;

    @SerializedName("user_metadata")
    private UserMetadata userMetadata;

    public String       getId()               { return id; }
    public String       getEmail()            { return email; }
    public String       getRole()             { return role; }
    public String       getEmailConfirmedAt() { return emailConfirmedAt; }
    public UserMetadata getUserMetadata()     { return userMetadata; }
}
