package com.aloha.linaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * LoveAppTest class.
 *
 * @author linfeng
 * @date 2026/7/25
 */
@SpringBootTest
public class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        String message = "我很喜欢美羊羊，怎么让她喜欢我？";
        String result1 = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(result1);

//        message = "除了这些之外呢？";
//        String result2 = loveApp.doChat(message, chatId);
//        Assertions.assertNotNull(result2);
//
//        message = "刚刚我给你说的我喜欢谁来着？";
//        String result3 = loveApp.doChat(message, chatId);
//        Assertions.assertNotNull(result3);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我很喜欢美羊羊，怎么让她喜欢我？";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void testDoChatWithRAG() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，该怎么跟老婆加强关系";
        String answer = loveApp.doChatWithRAG(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
