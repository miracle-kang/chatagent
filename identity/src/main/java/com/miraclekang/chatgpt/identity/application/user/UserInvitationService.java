package com.miraclekang.chatgpt.identity.application.user;

import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.application.user.querystack.InvitationAccepterDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.InvitationDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.InvitationDetailDTO;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationId;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationRepository;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationType;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InviteCodeGenerator;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class UserInvitationService {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    public UserInvitationService(UserRepository userRepository, InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    public Mono<InvitationDTO> newInvitation(InvitationType type) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new UserId(requester.getUserId()))
                        .publishOn(Schedulers.boundedElastic())
                        .map(userRepository::findByUserId)
                        .map(user -> invitationRepository.save(user
                                .newInvitation(type, new InviteCodeGenerator(invitationRepository)))))
                .map(InvitationDTO::from);
    }

    public Mono<Page<InvitationDTO>> queryUserInvitations(Pageable pageable) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new UserId(requester.getUserId()))
                        .publishOn(Schedulers.boundedElastic())
                        .map(user -> invitationRepository.queryByInviterId(new UserId(requester.getUserId()), pageable)))
                .map(page -> page.map(InvitationDTO::from));
    }

    public Mono<InvitationDetailDTO> getInvitationDetail(String anInvitationId) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new InvitationId(anInvitationId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(invitationId -> invitationRepository
                                .findByInvitationIdAndInviterId(invitationId, new UserId(requester.getUserId())))
                        .switchIfEmpty(Mono.error(new IllegalStateException("Invitation not found")))
                        .map(invitation -> new InvitationDetailDTO(
                                invitation.getInvitationId().getId(),
                                invitation.getInviteCode(),
                                invitation.getInvitationType(),
                                invitation.getInvitationStatus(),
                                userRepository.top10InvitationUsers(invitation.getInvitationId()).stream()
                                        .map(InvitationAccepterDTO::from)
                                        .toList()
                        )));
    }

    public Mono<InvitationDTO> invalidateInvitation(String anInvitationId) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new InvitationId(anInvitationId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(invitationId -> invitationRepository
                                .findByInvitationIdAndInviterId(invitationId, new UserId(requester.getUserId())))
                        .switchIfEmpty(Mono.error(new IllegalStateException("Invitation not found")))
                        .map(invitation -> {
                            invitation.invalid(requester);
                            return invitationRepository.save(invitation);
                        }))
                .map(InvitationDTO::from);
    }

    public Mono<Page<InvitationAccepterDTO>> queryInvitationAcceptors(String anInvitationId, Pageable pageable) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new InvitationId(anInvitationId))
                        .publishOn(Schedulers.boundedElastic())
                        .mapNotNull(invitationId -> invitationRepository
                                .findByInvitationIdAndInviterId(invitationId, new UserId(requester.getUserId())))
                        .switchIfEmpty(Mono.error(new IllegalStateException("Invitation not found")))
                        .map(invitation -> userRepository.queryInvitationUsers(invitation.getInvitationId(), pageable)))
                .map(page -> page.map(InvitationAccepterDTO::from));
    }
}

