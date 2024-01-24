package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class AdministratorRootStrategy {

    private final UserRepository userRepository;
    private final AdministratorRepository administratorRepository;

    private final RegisterService userRegisterService;

    private final Set<UserId> rootUserIds = new HashSet<>();

    public AdministratorRootStrategy(UserRepository userRepository,
                                     AdministratorRepository administratorRepository,
                                     RegisterService userRegisterService) {
        this.userRepository = userRepository;
        this.userRegisterService = userRegisterService;
        this.administratorRepository = administratorRepository;
    }

    public void assignRootUsers(List<String> rootPhoneNumbers, boolean autoInit) {
        rootUserIds.addAll(rootPhoneNumbers.stream().map(phoneNumber -> {
            if (!Phone.isValidPhoneNumber(phoneNumber)) {
                log.warn(">>>>>> Invalid root phone number format: {}", phoneNumber);
                return null;
            }
            Phone phone = new Phone(phoneNumber);
            User user = userRepository.findByPhone(phone);
            if (user != null && user.getUserType() == UserType.Administrator) {
                return user.getUserId();
            }
            if (autoInit) {
                Username rootUsername = new Username("root-" + phone.tail(4));
                Administrator administrator = userRegisterService.registerAdmin(null,
                        rootUsername,
                        null,
                        phone,
                        "666666",
                        false,
                        UserProfile.defaultProfile(rootUsername.getUsername())
                );
                administratorRepository.save(administrator);
                log.info(">>>>>> Initialized root administrator: {}", administrator);
                return administrator.getUser().getUserId();
            }
            log.warn(">>>>>> Administrator user phone number {} not exists", phoneNumber);
            return null;
        }).filter(Objects::nonNull).toList());
    }

    public boolean isRootUser(UserId userId) {
        if (userId == null) {
            return false;
        }
        return rootUserIds.contains(userId);
    }
}
