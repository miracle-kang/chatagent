package com.miraclekang.chatgpt.subscription.domain.model.equity;

import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEquityRepository extends JpaRepository<UserEquity, Long>,
        JpaSpecificationExecutor<UserEquity> {

    UserEquity findByUserEquityId(UserEquityId userEquityId);

    default List<UserEquity> userEquities(UserId userId) {
        return findAll((root, query, builder) -> builder.and(
                        builder.equal(root.get(UserEquity_.ownerUserId), userId),
                        builder.greaterThan(root.get(UserEquity_.expiresTime), LocalDateTime.now().minusMonths(3))
                ),
                Sort.by(UserEquity_.ID).descending());
    }
}
