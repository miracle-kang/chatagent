package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    User findByUserId(UserId userId);

    User findByPhone(Phone phone);

    User findByEmail(Email email);

    User findByUsername(Username username);

    default User findByIdentity(String identity) {
        if (Phone.isValidPhoneNumber(identity)) {
            return findByPhone(Phone.of(identity));
        } else if (Email.isValidEmailAddress(identity)) {
            return findByEmail(Email.of(identity));
        } else if (Username.isValidUsername(identity)) {
            return findByUsername(Username.of(identity));
        } else {
            return null;
        }
    }

    default List<User> top10InvitedUsers(UserId inviter) {
        return invitedUsers(inviter, PageRequest.of(0, 10))
                .getContent();
    }

    default Page<User> invitedUsers(UserId inviter, Pageable pageable) {
        Specification<User> specification = ((root, query, builder) ->
                builder.equal(root.get(User_.inviter), inviter));

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(User_.ID).descending()));
        return findAll(specification, pageRequest);
    }

    default List<User> top10InvitationUsers(InvitationId invitationId) {
        return queryInvitationUsers(invitationId, PageRequest.of(0, 10))
                .getContent();
    }

    default Page<User> queryInvitationUsers(InvitationId invitationId, Pageable pageable) {
        Specification<User> specification = ((root, query, builder) ->
                builder.equal(root.get(User_.invitationId), invitationId));

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSortOr(Sort.by(User_.ID).descending()));
        return findAll(specification, pageRequest);
    }

    @Query("""
            SELECT u.username
            FROM User u
            WHERE u.userId = :userId
            """)
    Username username(UserId userId);

    boolean existsByUserId(UserId userId);

    boolean existsByUsername(Username username);

    boolean existsByPhone(Phone phone);

    boolean existsByEmail(Email email);
}
