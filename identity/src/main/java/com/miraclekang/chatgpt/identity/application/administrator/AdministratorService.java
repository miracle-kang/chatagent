package com.miraclekang.chatgpt.identity.application.administrator;

import com.miraclekang.chatgpt.identity.application.administrator.command.NewAdministratorCommand;
import com.miraclekang.chatgpt.identity.application.administrator.command.UpdateAdministratorCommand;
import com.miraclekang.chatgpt.identity.application.administrator.querystack.AdministratorDTO;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.SearchCriteria;
import com.miraclekang.chatgpt.common.repo.SearchSpecification;
import com.miraclekang.chatgpt.identity.domain.model.identity.*;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.PasswordService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserIdentityCheckService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.Username;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class AdministratorService {

    private final RegisterService registerService;
    private final UserIdentityCheckService identityCheckService;
    private final AdministratorRootStrategy rootStrategy;
    private final PasswordService passwordService;

    private final AdministratorRepository administratorRepository;

    public AdministratorService(RegisterService registerService,
                                UserIdentityCheckService identityCheckService,
                                AdministratorRootStrategy rootStrategy,
                                PasswordService passwordService, AdministratorRepository administratorRepository) {
        this.registerService = registerService;
        this.identityCheckService = identityCheckService;
        this.rootStrategy = rootStrategy;
        this.passwordService = passwordService;
        this.administratorRepository = administratorRepository;
    }

    @Transactional
    public Mono<AdministratorDTO> addAdministrator(NewAdministratorCommand command) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    var administrator = registerService.registerAdmin(
                            requester,
                            Username.of(command.getUsername()),
                            Email.of(command.getEmailAddress()),
                            Phone.of(command.getPhoneNumber()),
                            command.getPassword(),
                            command.getDisabled(),
                            command.getProfile() == null ? null : command.getProfile().toProfile()
                    );
                    return administratorRepository.save(administrator);
                })
                .map(AdministratorDTO::from);
    }

    @Transactional
    public Mono<AdministratorDTO> updateAdministrator(String anAdministratorId, UpdateAdministratorCommand command) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(administratorId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(administratorRepository::findByAdministratorId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                        .map(administrator -> {
                            administrator.update(
                                    Username.of(command.getUsername()),
                                    Email.of(command.getEmailAddress()),
                                    Phone.of(command.getPhoneNumber()),
                                    command.getProfile() == null ? null : command.getProfile().toProfile(),
                                    requester, identityCheckService
                            );
                            return administratorRepository.save(administrator);
                        }).map(AdministratorDTO::from));
    }

    public Mono<AdministratorDTO> getAdministrator(String anAdministratorId) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(requester -> administratorRepository.findByAdministratorId(administratorId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                .map(AdministratorDTO::from);
    }

    @Transactional
    public Mono<Void> deleteAdministrator(String anAdministratorId) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(administratorId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(administratorRepository::findByAdministratorId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                        .flatMap(administrator -> {
                            if (administrator.isRoot(rootStrategy)) {
                                return Mono.error(new IllegalArgumentException("Root administrator cannot be deleted"));
                            }
                            return blockingOperation(() -> administratorRepository.delete(administrator));
                        })
                );
    }

    public Mono<Page<AdministratorDTO>> queryAdministrators(List<SearchCriteria> searchCriteria, Pageable pageable) {
        return Requester.currentRequester()
                .publishOn(Schedulers.boundedElastic())
                .map(requester -> {
                    PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                            Sort.by(Administrator_.ID).descending());

                    return administratorRepository.findAll(SearchSpecification.allOf(searchCriteria), pageRequest)
                            .map(AdministratorDTO::from);
                });
    }

    @Transactional
    public Mono<Void> disableAdministrator(String anAdministratorId) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(administratorId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(administratorRepository::findByAdministratorId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                        .map(administrator -> {
                            administrator.disable(requester, rootStrategy);
                            return administratorRepository.save(administrator);
                        }).then());
    }

    @Transactional
    public Mono<Void> enableAdministrator(String anAdministratorId) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(administratorId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(administratorRepository::findByAdministratorId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                        .map(administrator -> {
                            administrator.enable(requester);
                            return administratorRepository.save(administrator);
                        }).then());
    }

    @Transactional
    public Mono<Void> resetAdministratorPassword(String anAdministratorId) {
        AdministratorId administratorId = new AdministratorId(anAdministratorId);
        return Requester.currentRequester()
                .flatMap(requester -> Mono.just(administratorId)
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(administratorRepository::findByAdministratorId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Administrator not found")))
                        .map(administrator -> {
                            administrator.resetPassword(requester, passwordService);
                            return administratorRepository.save(administrator);
                        }).then());
    }
}
