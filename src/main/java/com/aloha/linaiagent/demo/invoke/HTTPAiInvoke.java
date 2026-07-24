package com.aloha.linaiagent.demo.invoke;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONObject;

/**
 * 通过HTTP的方式调用大模型
 *
 * @author linfeng
 * @date 2026/7/24
 */
public class HTTPAiInvoke {

    private static final String API_URL = "https://ws-j07cttolaa7dklcu.cn-beijing.maas.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";
    private static final String MODEL = "qwen3.6-flash";
    private static final String IMAGE_URL = "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg";
    private static final String PROMPT = "图中描绘的是什么景象?";

    public static void main(String[] args) {
        String apiKey = TestApiKey.API_KEY;
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("请先设置环境变量 DASHSCOPE_API_KEY");
        }

        String result = invoke(apiKey);
        System.out.println(result);
    }

    public static String invoke(String apiKey) {
        JSONObject body = buildBody();
        try (HttpResponse response = HttpRequest.post(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(ContentType.JSON.getValue())
                .body(body.toString())
                .timeout(60_000)
                .execute()) {

            String responseBody = response.body();
            if (!response.isOk()) {
                throw new IllegalStateException("请求失败，HTTP " + response.getStatus() + "，响应：" + responseBody);
            }
            return extractText(responseBody);
        }
    }

    private static JSONObject buildBody() {
        JSONArray content = new JSONArray();
        content.add(JSONUtil.createObj().set("image", IMAGE_URL));
        content.add(JSONUtil.createObj().set("text", PROMPT));

        JSONObject message = JSONUtil.createObj()
                .set("role", "user")
                .set("content", content);

        JSONArray messages = new JSONArray();
        messages.add(message);

        JSONObject input = JSONUtil.createObj()
                .set("messages", messages);

        return JSONUtil.createObj()
                .set("model", MODEL)
                .set("input", input);
    }

    private static String extractText(String responseBody) {
        JSONObject root = JSONUtil.parseObj(responseBody);
        JSONArray choices = root.getByPath("output.choices", JSONArray.class);
        if (choices == null || choices.isEmpty()) {
            return responseBody;
        }
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONArray messageContent = firstChoice.getByPath("message.content", JSONArray.class);
        if (messageContent == null || messageContent.isEmpty()) {
            return responseBody;
        }
        JSONObject firstContent = messageContent.getJSONObject(0);
        String text = firstContent.getStr("text");
        return text == null || text.isBlank() ? responseBody : text;
    }
}
