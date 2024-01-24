package com.miraclekang.chatgpt.identity.domain.model.identity.invitation;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long>,
        JpaSpecificationExecutor<Invitation> {

    List<Invitation> findByInviterId(UserId inviterId);

    default Page<Invitation> queryByInviterId(UserId inviterId, Pageable pageable) {
        Specification<Invitation> specification = ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Invitation_.inviterId), inviterId));
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Invitation_.ID).descending()));

        return findAll(specification, pageRequest);
    }

    boolean existsByInviteCode(String inviteCode);

    Invitation findByInviteCode(String inviteCode);

    Invitation findByInvitationIdAndInviterId(InvitationId invitationId, UserId inviterId);
}
