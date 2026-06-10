package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

// Memetakan field "user_metadata" di dalam objek user Supabase.
// Field ini diisi saat signup via SignUpRequest.data.full_name.
public class UserMetadata {

    @SerializedName("full_name")
    private String fullName;

    public String getFullName() { return fullName; }
}
