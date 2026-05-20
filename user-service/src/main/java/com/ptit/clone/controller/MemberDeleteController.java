package com.ptit.clone.controller;

import com.ptit.clone.service.IDeleteMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Slf4j
public class MemberDeleteController {

    private final IDeleteMemberService deleteMemberService;

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        log.info("Delete member id={}", memberId);
        deleteMemberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
