package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {


    public AuthorizationService() {
    }

    /**
     * 检查操作是否授权
     *
     * @param operation 操作项
     */
    public boolean authorizedOperation(UserId userId, Operation operation) {
        return true;
    }
}
