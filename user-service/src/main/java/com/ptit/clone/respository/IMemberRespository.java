package com.ptit.clone.respository;

import com.ptit.clone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMemberRespository extends JpaRepository<Member, Long> {
    Optional<Member> findByKeycloakUserId(String keycloakUserId);
    Optional<Member> findByEmailIgnoreCase(String email);
}
