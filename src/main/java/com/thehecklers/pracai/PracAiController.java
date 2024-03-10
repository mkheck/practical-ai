package com.thehecklers.pracai;

import org.springframework.ai.azure.openai.AzureOpenAiChatClient;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class PracAiController {
    private final AzureOpenAiChatClient client;

    private final List<Message> buffer = new ArrayList<>();

    public PracAiController(AzureOpenAiChatClient client) {
        this.client = client;
    }

    @GetMapping
    public ChatResponse chat(@RequestParam(defaultValue = "Tell me a joke") String message,
                             @RequestParam(required = false) String celebrity) {

        var promptMessages = new ArrayList<Message>(buffer);
        promptMessages.add(new UserMessage(message));

        if (null != celebrity) {
            var sysTemplate = new SystemPromptTemplate("You respond in the style of {celebrity}.");
            Message sysMessage = sysTemplate.createMessage(Map.of("celebrity", celebrity));
            promptMessages.add(sysMessage);
        }

        ChatResponse response = client.call(new Prompt(promptMessages));
        buffer.add(response.getResult().getOutput());
        return response;
    }

    @GetMapping("/template")
    public String useTemplate(@RequestParam String type, @RequestParam String topic) {
        PromptTemplate template = new PromptTemplate("Write me a {type} about {topic}");
        Prompt prompt = template.create(Map.of("type", type, "topic", topic));
        return client.call(prompt).getResult().getOutput().getContent();
    }

    @GetMapping("/weather")
    public AssistantMessage weather(@RequestParam(defaultValue = "Philadelphia") String location) {
        return client.call(new Prompt(
                        new UserMessage("What is the weather in " + location),
                        AzureOpenAiChatOptions.builder()
                                .withFunction("weatherFunction")
                                .build()))
                .getResult()
                .getOutput();
    }

    @GetMapping("/about")
    public String about(@RequestParam(defaultValue = "What are some recommended activities?") String message,
                        @RequestParam(defaultValue = "Philadelphia") String location) {
        var assistantMessage = weather(location);

        PromptTemplate template = new PromptTemplate("{message} in {location}");
        Message userMessage = template.createMessage(Map.of("message", message, "location", location));

        return client.call(new Prompt(List.of(assistantMessage, userMessage)))
                .getResult()
                .getOutput()
                .getContent();
    }
}
