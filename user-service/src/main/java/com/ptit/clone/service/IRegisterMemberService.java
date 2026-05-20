package com.ptit.clone.service;

import com.ptit.clone.dtos.request.MemberRegisterRequest;

public interface IRegisterMemberService {
    void registerMember (MemberRegisterRequest memberRegisterRequest);
    void markMemberEmailVerified(String keycloakUserId);
}
