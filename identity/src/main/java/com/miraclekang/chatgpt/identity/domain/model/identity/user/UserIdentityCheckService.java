package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserIdentityCheckService {

    private final UserRepository userRepository;

    public UserIdentityCheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void checkDuplicate(User user, Username username, Email email, Phone phone) {

        if (username != null && (user == null || !Objects.equals(username, user.getUsername()))
                && userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (email != null && (user == null || !Objects.equals(email, user.getEmail()))
                && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }

        if (phone != null && (user == null || !Objects.equals(phone, user.getPhone()))
                && userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Phone already exists.");
        }
    }
}
