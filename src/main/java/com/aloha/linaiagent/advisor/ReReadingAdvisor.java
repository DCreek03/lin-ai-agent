package com.aloha.linaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * ReReadingAdvisor class.
 *
 * @author linfeng
 * @date 2026/7/25
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor{

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        Map<String, Object> adviseUserParams = new HashMap<>(advisedRequest.userParams());
        adviseUserParams.put("re2_input_query", advisedRequest.userText());
        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again:{re2_input_query}
                        """)
                .userParams(adviseUserParams)
                .build();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
