package com.aloha.linaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SpringAIInvoke class.
 *
 * @author linfeng
 * @date 2026/7/24
 */
@Component
public class SpringAIInvoke implements CommandLineRunner {

    @Resource
    private ChatModel chatModel;

    @Override
    public void run(String... args) throws Exception {



    }
}
