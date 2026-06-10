package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class SignUpRequest {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // Field "data" disimpan sebagai user_metadata di Supabase
    @SerializedName("data")
    private Map<String, String> data;

    public SignUpRequest(String email, String password, String fullName) {
        this.email    = email;
        this.password = password;
        this.data     = new HashMap<>();
        this.data.put("full_name", fullName);
    }
}
