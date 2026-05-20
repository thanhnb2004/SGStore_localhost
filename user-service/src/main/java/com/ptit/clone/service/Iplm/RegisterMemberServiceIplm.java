package com.ptit.clone.service.Iplm;

import com.ptit.clone.config.keycloak.KeycloakAdminClientFactory;
import com.ptit.clone.config.properties.KeycloakProperties;
import com.ptit.clone.entity.Member;
import com.ptit.clone.mapper.MemberMapper;
import com.ptit.clone.model.MemberStatus;
import com.ptit.clone.model.RequiredAcionsKC;
import com.ptit.clone.dtos.request.MemberRegisterRequest;
import com.ptit.clone.respository.IMemberRespository;
import com.ptit.clone.service.IRegisterMemberService;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterMemberServiceIplm implements IRegisterMemberService {

    private final IMemberRespository memberRespository;
    private final KeycloakAdminClientFactory keycloakAdminClientFactory;
    private final KeycloakProperties keycloakProperties;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void registerMember(MemberRegisterRequest memberRegisterRequest) {
        String keycloakUserID = createUserAndSendVerification(memberRegisterRequest);
        Member member = memberMapper.toEntity(memberRegisterRequest);
        member.setEmail(memberRegisterRequest.getEmail().trim().toLowerCase(Locale.ROOT));
        member.setPasswordHash(passwordEncoder.encode(memberRegisterRequest.getPassword()));
        member.setKeycloakUserId(keycloakUserID);
        memberRespository.save(member);
    }

    @Override
    public void markMemberEmailVerified(String keycloakUserId) {
        Member member = memberRespository.findByKeycloakUserId(keycloakUserId).orElse(null);
        if (member == null) {
            log.warn("Skip mark ACTIVE because member not found for keycloakUserId={}", keycloakUserId);
            return;
        }

        if (MemberStatus.ACTIVE.equals(member.getStatus())) {
            log.info("Member already ACTIVE, skipping duplicate verify-email event keycloakUserId={}", keycloakUserId);
            return;
        }

        member.setStatus(MemberStatus.ACTIVE);
        member.setEmailVerifiedAt(Instant.now());
        memberRespository.save(member);
        log.info("Updated member status to ACTIVE for keycloakUserId={}", keycloakUserId);
    }


    // helper

    public String createUserAndSendVerification(MemberRegisterRequest request) {
        Keycloak keycloak = keycloakAdminClientFactory.buildAdminClient();
        try {
            UsersResource usersResource = keycloak.realm(keycloakProperties.getRealm()).users();
            String userId = createUser(usersResource, request);
            usersResource.get(userId).executeActionsEmail(List.of(RequiredAcionsKC.VERIFY_EMAIL.name()));
            return userId;
        } finally {
            keycloak.close();
        }
    }

    private String createUser(UsersResource usersResource, MemberRegisterRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRequiredActions(List.of(RequiredAcionsKC.VERIFY_EMAIL.name()));


        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(request.getPassword());
        passwordCredential.setTemporary(false);
        user.setCredentials(List.of(passwordCredential));

        try (Response response = usersResource.create(user)) {
            int status = response.getStatus();
            if (status == 201) {
                URI location = response.getLocation();
                if (location == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Keycloak response missing Location");
                }
                return extractUserId(location.toString());
            }
            if (status == 409) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists in Keycloak");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot create user in Keycloak");
        }

    }

    private static String extractUserId(String locationHeader) {
        String path = URI.create(locationHeader).getPath();
        int i = path.lastIndexOf('/');
        if (i < 0 || i == path.length() - 1) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot parse Keycloak user id");
        }
        return path.substring(i + 1);
    }
}


