package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRegistrationRepository extends JpaRepository<UserRegistration, Long> {

    UserRegistration findByRegistrationId(UserRegistrationId registrationId);

    List<UserRegistration> findByUserId(UserId userId);
}
