package com.ptit.clone.controller;


import com.ptit.clone.dtos.request.MemberRegisterRequest;
import com.ptit.clone.service.IRegisterMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberRegisterController {

    private final IRegisterMemberService memberService;

    @PostMapping("/")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberRegisterRequest request) {
        log.info("EMAIL: {}", request.getEmail());
        memberService.registerMember(request);
        return ResponseEntity.noContent().build();
    }
}
