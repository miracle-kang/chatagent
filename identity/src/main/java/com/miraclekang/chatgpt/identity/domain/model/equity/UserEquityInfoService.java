package com.miraclekang.chatgpt.identity.domain.model.equity;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;

import java.util.List;

public interface UserEquityInfoService {

    List<UserEquityInfo> userEquities(UserId userId);
}
