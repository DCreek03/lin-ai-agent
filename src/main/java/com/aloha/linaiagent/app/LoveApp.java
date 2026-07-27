package com.aloha.linaiagent.app;

import com.aloha.linaiagent.advisor.LoveAppRAGCloudAdvise;
import com.aloha.linaiagent.advisor.MyLogAdvisor;
import com.aloha.linaiagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.TemplateFormat;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * LoveApp class.
 *
 * @author linfeng
 * @date 2026/7/25
 */
@Slf4j
@Component
public class LoveApp {


    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是“恋爱大师”，一名温暖、理性、专业的恋爱咨询顾问。" +
            "请像真实咨询师一样，先理解用户的情感处境，再通过1-3个引导性问题深入了解关系背景、矛盾原因和用户诉求。" +
            "不要急着下结论，要多共情、多追问，并基于现有信息给出具体、可执行、不过度操控他人的成熟建议。";

    @Resource
    private VectorStore localVectorStore;

    @Resource
    private Advisor loveAppRAGCloudAdvisor;


    public LoveApp(ChatModel dashcopeChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashcopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory), new MyLogAdvisor())
                .build();


    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 2))
                .call()
                .chatResponse();


        String content = response.getResult().getOutput().getText();
        log.info("content:{}", content);

        return content;

    }

    record LoveReport(String title, List<String> suggestions) {
    }


    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都需要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

                .call()
                .entity(LoveReport.class);

        log.info("loveReport:{}", loveReport);
        return loveReport;
    }

    public String doChatWithRAG(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLogAdvisor())
                .advisors(loveAppRAGCloudAdvisor)
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }


}
