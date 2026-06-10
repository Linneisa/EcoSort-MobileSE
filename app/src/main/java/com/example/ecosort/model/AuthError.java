package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

// Menangani beberapa format error dari Supabase Auth:
//   {"error":"invalid_grant","error_description":"Invalid login credentials"}
//   {"code":422,"msg":"User already registered"}
public class AuthError {

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String errorDescription;

    @SerializedName("msg")
    private String msg;

    @SerializedName("message")
    private String message;

    public String getMessage() {
        if (isNotEmpty(errorDescription)) return errorDescription;
        if (isNotEmpty(msg))              return msg;
        if (isNotEmpty(error))            return error;
        if (isNotEmpty(message))          return message;
        return "Terjadi kesalahan, silakan coba lagi";
    }

    private boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
