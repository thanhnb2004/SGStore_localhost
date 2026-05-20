package com.ptit.clone.mapper;

import com.ptit.clone.entity.Member;
import com.ptit.clone.model.MemberStatus;
import com.ptit.clone.dtos.request.MemberRegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public Member toEntity (MemberRegisterRequest memberRegisterRequest){
        return Member.builder()
                .firstName(memberRegisterRequest.getFirstName())
                .lastName(memberRegisterRequest.getLastName())
                .email(memberRegisterRequest.getEmail())
                .phone(memberRegisterRequest.getPhone())
                .status(MemberStatus.PENDING)
                .build();
    }
}
