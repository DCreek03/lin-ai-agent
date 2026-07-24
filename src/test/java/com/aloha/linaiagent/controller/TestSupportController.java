package com.aloha.linaiagent.controller;

import com.aloha.linaiagent.common.exception.BizException;
import com.aloha.linaiagent.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestSupportController {

    @GetMapping("/ok")
    public ApiResponse<Map<String, String>> ok() {
        return ApiResponse.ok(Map.of("message", "ok"));
    }

    @PostMapping("/body")
    public ApiResponse<BodyEcho> body(HttpServletRequest request) throws IOException {
        String first = readBody(request);
        String second = readBody(request);
        return ApiResponse.ok(new BodyEcho(first, second));
    }

    @PostMapping("/validate")
    public ApiResponse<TestPayload> validate(@Valid @RequestBody TestPayload payload) {
        return ApiResponse.ok(payload);
    }

    @GetMapping("/type-mismatch")
    public ApiResponse<Integer> typeMismatch(@RequestParam Integer age) {
        return ApiResponse.ok(age);
    }

    @GetMapping("/biz-error")
    public ApiResponse<Void> bizError() {
        throw new BizException("40000", "business failure");
    }

    private String readBody(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public record BodyEcho(String first, String second) {
    }

    public record TestPayload(@NotBlank(message = "name is required") String name) {
    }
}
