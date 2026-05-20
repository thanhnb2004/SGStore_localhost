package com.ptit.clone.service;

import com.ptit.clone.dtos.request.LoginRequest;
import com.ptit.clone.dtos.response.LoginResponse;

public interface ILoginMemberService {
    LoginResponse login(LoginRequest request);
}
