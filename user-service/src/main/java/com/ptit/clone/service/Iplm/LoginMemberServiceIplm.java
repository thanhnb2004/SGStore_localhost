package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.LoginRequest;
import com.ptit.clone.dtos.response.LoginResponse;
import com.ptit.clone.entity.Member;
import com.ptit.clone.model.MemberStatus;
import com.ptit.clone.respository.IMemberRespository;
import com.ptit.clone.service.ILoginMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginMemberServiceIplm implements ILoginMemberService {

    private final IMemberRespository memberRespository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        Member member = memberRespository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        String hash = member.getPasswordHash();
        if (hash == null || hash.isBlank()
                || !passwordEncoder.matches(request.getPassword(), hash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!MemberStatus.ACTIVE.equals(member.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        String fullName = (member.getFirstName() + " " + member.getLastName()).trim();
        return LoginResponse.builder()
                .userId(member.getId())
                .fullName(fullName)
                .build();
    }
}
