package com.aloha.linaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

/**
 * LangChain4jInvoke class.
 *
 * @author linfeng
 * @date 2026/7/24
 */
public class LangChain4jInvoke {
    public static void main(String[] args) {
        QwenChatModel build = QwenChatModel.builder().apiKey(TestApiKey.API_KEY).modelName("qwen3.7-max").build();
        String helloWorld = build.chat("hello world");
        System.out.println(helloWorld);
    }
}
