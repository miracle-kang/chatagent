package com.miraclekang.chatgpt.assistant.domain.model.equity;

import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;

import java.util.List;

public interface UserEquityInfoService {

    List<UserEquityInfo> userEquities(UserId userId);
}
