package com.example.ecosort.network;

import com.example.ecosort.model.AuthRequest;
import com.example.ecosort.model.AuthResponse;
import com.example.ecosort.model.SignUpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    // Sign In: POST /auth/v1/token?grant_type=password
    @POST("token?grant_type=password")
    Call<AuthResponse> signIn(@Body AuthRequest request);

    // Sign Up: POST /auth/v1/signup
    @POST("signup")
    Call<AuthResponse> signUp(@Body SignUpRequest request);
}
