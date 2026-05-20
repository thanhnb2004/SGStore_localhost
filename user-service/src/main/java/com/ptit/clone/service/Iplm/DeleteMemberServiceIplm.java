package com.ptit.clone.service.Iplm;

import com.ptit.clone.config.keycloak.KeycloakAdminClientFactory;
import com.ptit.clone.config.properties.KeycloakProperties;
import com.ptit.clone.entity.Member;
import com.ptit.clone.respository.IMemberRespository;
import com.ptit.clone.service.IDeleteMemberService;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteMemberServiceIplm implements IDeleteMemberService {

    private final IMemberRespository memberRespository;
    private final KeycloakAdminClientFactory keycloakAdminClientFactory;
    private final KeycloakProperties keycloakProperties;

    @Override
    public void deleteMember(Long memberId) {
        Member member = memberRespository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        if (member.getKeycloakUserId() != null) {
            deleteMemberInKeycloak(member.getKeycloakUserId());
        }
        memberRespository.delete(member);
        log.info("Deleted member id={} keycloakUserId={}", memberId, member.getKeycloakUserId());
    }

    private void deleteMemberInKeycloak(String keycloakUserId) {
        Keycloak keycloak = keycloakAdminClientFactory.buildAdminClient();
        try {
            keycloak.realm(keycloakProperties.getRealm()).users().get(keycloakUserId).remove();
        } catch (NotFoundException ex) {
            log.warn("Skip Keycloak deletion because user not found keycloakUserId={}", keycloakUserId);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cannot delete user in Keycloak", ex);
        } finally {
            keycloak.close();
        }
    }
}
