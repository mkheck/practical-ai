package com.thehecklers.pracai;

import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
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
    private final ChatClient client;

    private final List<Message> buffer = new ArrayList<>();

    public PracAiController(ChatClient.Builder builder) {
        this.client = builder.build();
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

        ChatResponse response = client.prompt(new Prompt(promptMessages))
                .call()
                .chatResponse();

        buffer.add(response.getResult().getOutput());

        return response;
    }

    @GetMapping("/template")
    public String useTemplate(@RequestParam String type, @RequestParam String topic) {
        var template = new PromptTemplate("Write me a {type} about {topic}",
                Map.of("type", type, "topic", topic));

        return client.prompt(template.create())
                .call()
                .content();
    }

    @GetMapping("/weather")
    public AssistantMessage weather(@RequestParam(defaultValue = "Philadelphia") String location) {
        return client.prompt(new Prompt(
                        new UserMessage("What is the weather in " + location),
                        AzureOpenAiChatOptions.builder()
                                .withFunction("weatherFunction")
                                .build()))
                .call()
                .chatResponse()
                .getResult()
                .getOutput();
    }

    @GetMapping("/about")
    public String about(@RequestParam(defaultValue = "What are some recommended activities?") String message,
                        @RequestParam(defaultValue = "Philadelphia") String location) {
        var assistantMessage = weather(location);

//        PromptTemplate template = new PromptTemplate("{message} in {location}");
//        Message userMessage = template.createMessage(Map.of("message", message, "location", location));
        var template = new PromptTemplate("{message} in {location}",
                Map.of("message", message, "location", location));
        Message userMessage = template.createMessage();

        return client.prompt(new Prompt(List.of(assistantMessage, userMessage)))
                .call()
                .content();
    }
}
