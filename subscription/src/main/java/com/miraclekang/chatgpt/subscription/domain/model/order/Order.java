package com.miraclekang.chatgpt.subscription.domain.model.order;

import com.miraclekang.chatgpt.subscription.domain.model.identity.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Table(indexes = {
        @Index(name = "UK_order_id", columnList = "order_id", unique = true),
        @Index(name = "IDX_user_id", columnList = "user_id")
})
public class Order {

    // @Comment("订单ID")
    @AttributeOverride(name = "id", column = @Column(name = "order_id", length = 64, nullable = false))
    private OrderId orderId;

    // @Comment("用户ID")
    private UserId userId;


}
