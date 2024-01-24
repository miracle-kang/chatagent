package com.miraclekang.chatgpt.identity.application.user;

import com.miraclekang.chatgpt.common.access.AuthenticateToken;
import com.miraclekang.chatgpt.common.access.TokenService;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.application.user.command.RegisterCommand;
import com.miraclekang.chatgpt.identity.domain.model.identity.*;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.Invitation;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationRepository;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final InvitationRepository invitationRepository;
    private final UserRegistrationRepository registrationRepository;

    private final TokenService tokenService;
    private final PasswordService passwordService;
    private final VerificationService verificationService;
    private final RegisterService registerService;

    public AuthenticationService(UserRepository userRepository,
                                 CustomerRepository customerRepository,
                                 InvitationRepository invitationRepository,
                                 UserRegistrationRepository registrationRepository,
                                 TokenService tokenService,
                                 PasswordService passwordService,
                                 VerificationService verificationService,
                                 RegisterService registerService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.invitationRepository = invitationRepository;
        this.registrationRepository = registrationRepository;

        this.tokenService = tokenService;
        this.passwordService = passwordService;
        this.verificationService = verificationService;
        this.registerService = registerService;
    }

    /**
     * 验证手机号是否存在
     *
     * @param phoneArea   手机区号
     * @param phoneNumber 手机号码
     */
    public Mono<Boolean> checkPhone(String phoneArea, String phoneNumber) {
        Validate.notBlank(phoneArea, "Phone area must be provided.");
        Validate.notBlank(phoneArea, "Phone number must be provided.");

        return Mono.just(new Phone(phoneArea, phoneNumber))
                .map(userRepository::existsByPhone);
    }

    /**
     * 发送登录验证码
     *
     * @param phoneArea   手机区号
     * @param phoneNumber 手机号码
     */
    public Mono<Void> sendPhoneAuthCode(String phoneArea, String phoneNumber, VerificationType type) {
        Validate.notBlank(phoneArea, "Phone area must be provided.");
        Validate.notBlank(phoneArea, "Phone number must be provided.");

        return Mono.just(new Phone(phoneArea, phoneNumber))
                .flatMap(phone -> {
                    String authCode = verificationService.sendVerificationCode(phone, type);
                    if (authCode == null) {
                        log.error(">>>>>> Failed to send auth code to phone {}", phone);
                        return Mono.error(new RuntimeException("Failed to send auth code"));
                    }
                    log.info(">>>>>> Send auth code to phone {} success: {}", phone, authCode);
                    return Mono.empty();
                });
    }

    /**
     * 手机验证
     *
     * @param phoneArea   手机区号
     * @param phoneNumber 手机号码
     * @param authCode    验证码
     * @return 登录Token
     */
    @Transactional
    public Mono<AuthenticateToken> authenticateByPhone(String phoneArea, String phoneNumber, String authCode) {
        Validate.notBlank(phoneArea, "Phone area must be provided.");
        Validate.notBlank(phoneNumber, "Phone number must be provided.");
        Validate.notBlank(authCode, "Auth code must be provided.");

        return Mono.just(new Phone(phoneArea, phoneNumber))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userRepository::findByPhone)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid phone number")))
                .map(user -> {
                    var principal = user.loginByPhoneAuthCode(verificationService, authCode);
                    userRepository.save(user);

                    log.info(">>>>>> User {}({}) logged by phone.", principal.getName(), principal.getUserId());
                    return tokenService.generateToken(principal);
                });
    }

    /**
     * 邮箱验证是否存在
     *
     * @param anEmailAddress 邮箱地址
     */
    public Mono<Boolean> checkEmail(String anEmailAddress) {
        Validate.notBlank(anEmailAddress, "Email address must be provided.");

        return Mono.just(new Email(anEmailAddress))
                .map(userRepository::existsByEmail);
    }

    /**
     * 发送邮件验证码
     *
     * @param anEmailAddress 邮箱地址
     */
    public Mono<Void> sendEmailAuthCode(String anEmailAddress, VerificationType type) {
        Validate.notBlank(anEmailAddress, "Email address must be provided.");

        return Mono.just(new Email(anEmailAddress))
                .flatMap(email -> {
                    String authCode = verificationService.sendVerificationCode(email, type);
                    if (authCode == null) {
                        log.error(">>>>>> Failed to send auth code to email address '{}'", email);
                        return Mono.error(new RuntimeException("Failed to send auth code"));
                    }
                    log.info(">>>>>> Send auth code to email '{}' success: {}", email, authCode);
                    return Mono.empty();
                });
    }

    /**
     * 邮箱验证登录
     *
     * @param anEmailAddress 邮箱地址
     * @param authCode       验证码
     * @return 登录Token
     */
    @Transactional
    public Mono<AuthenticateToken> authenticateByEmail(String anEmailAddress, String authCode) {
        Validate.notBlank(anEmailAddress, "Email address must be provided.");
        Validate.notBlank(authCode, "Auth code must be provided.");

        return Mono.just(new Email(anEmailAddress))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userRepository::findByEmail)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid phone number")))
                .map(user -> {
                    var principal = user.loginByEmailAuthCode(verificationService, authCode);
                    userRepository.save(user);

                    log.info(">>>>>> User {}({}) logged by phone.", principal.getName(), principal.getUserId());
                    return tokenService.generateToken(principal);
                });
    }

    /**
     * 通过账号密码登录
     *
     * @param identity 手机或邮箱或用户名
     * @param password 密码
     * @return 登录Token
     */
    @Transactional
    public Mono<AuthenticateToken> authenticateByPassword(String identity, String password) {
        Validate.notBlank(identity, "Phone number or email address must be provided.");
        Validate.notBlank(password, "Password must be provided.");

        return Mono.just(identity)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(userRepository::findByIdentity)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid phone or email")))
                .map(user -> {
                    var principal = user.loginByPassword(passwordService, password);
                    userRepository.save(user);

                    log.info(">>>>>> User {}({}) logged by phone.", principal.getName(), principal.getUserId());
                    return tokenService.generateToken(principal);
                });
    }

    public Mono<AuthenticateToken> oauth2Authenticate(String aRegistration, OAuth2User oauth2User) {
        Objects.requireNonNull(aRegistration, "Registration must be provided.");
        Objects.requireNonNull(oauth2User, "OAuth2 user must be provided.");

        var registrationId = UserRegistrationId.of(Registration.fromValue(aRegistration), oauth2User.getName());

        return Mono.just(registrationId)
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(registrationRepository::findByRegistrationId)
                .switchIfEmpty(Mono.just(UserRegistration.of(registrationId, oauth2User.getAttributes())))
                .map(registration -> {
                    registration.update(oauth2User.getAttributes());
                    return registrationRepository.save(registration);
                })
                .flatMap(registration -> Mono.just(registration)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(UserRegistration::getUserId)
                        .switchIfEmpty(Requester.currentRequester().mapNotNull(Requester::getUserId).map(UserId::new))
                        .switchIfEmpty(Mono.fromCallable(() -> registerService.registrationRegisterCustomer(registration))
                                .publishOn(Schedulers.boundedElastic())
                                .map(customer -> customerRepository.save(customer).getUser().getUserId()))
                        .mapNotNull(userRepository::findByUserId)
                        .switchIfEmpty(Mono.error(new IllegalStateException("User not exists")))
                        .map(user -> {
                            if (!registration.bounded()) {
                                registration.boundTo(user);
                                registrationRepository.save(registration);
                            }
                            var principal = user.login();
                            userRepository.save(user);

                            log.info(">>>>>> User {}({}) logged by oauth {}.",
                                    principal.getName(), principal.getUserId(), aRegistration);
                            return tokenService.generateToken(principal);
                        }));
    }

    public Mono<AuthenticateToken> register(RegisterCommand command) {
        return Mono.just(command)
                .publishOn(Schedulers.boundedElastic())
                .filter(cmd -> cmd.getPhoneNumber() != null || cmd.getEmailAddress() != null)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Phone number or email address must be provided.")))
                .filter(cmd -> cmd.getInviteCode() == null || invitationRepository.existsByInviteCode(cmd.getInviteCode()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid invite code")))
                // TODO: AuthCode verification
//                .filter(cmd -> cmd.getPhoneNumber() == null ||
//                        verificationService.checkVerificationCode(
//                                Phone.of(cmd.getPhoneArea(), cmd.getPhoneNumber()),
//                                cmd.getPhoneAuthCode()))
                .filter(cmd -> cmd.getEmailAddress() == null ||
                        verificationService.checkVerificationCode(
                                Email.of(cmd.getEmailAddress()),
                                cmd.getEmailAuthCode()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid auth code")))
                .mapNotNull(cmd -> {
                    Invitation invitation = cmd.getInviteCode() == null ? null
                            : invitationRepository.findByInviteCode(cmd.getInviteCode());
                    Customer customer = registerService.invitationRegisterCustomer(
                            Username.of(cmd.getUsername()),
                            Email.of(cmd.getEmailAddress()),
                            Phone.of(cmd.getPhoneArea(), cmd.getPhoneNumber()),
                            cmd.getPassword(),
                            invitation,
                            cmd.getProfile() == null ? null : cmd.getProfile().toProfile());

                    var principal = customer.getUser().login();
                    customerRepository.save(customer);

                    log.info(">>>>>> User {}({}) registered with invite code {}.",
                            principal.getName(), principal.getUserId(), cmd.getInviteCode());
                    return tokenService.generateToken(principal);
                });
    }

    public Mono<AuthenticateToken> renewalToken() {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("User not logged")))
                .mapNotNull(requester -> userRepository.findByUserId(new UserId(requester.getUserId())))
                .switchIfEmpty(Mono.error(new IllegalStateException("User not exists")))
                .map(user -> {
                    var principal = user.login();
                    userRepository.save(user);

                    log.info(">>>>>> User {}({}) renewed token.", principal.getName(), principal.getUserId());
                    return tokenService.generateToken(principal);
                });
    }
}
