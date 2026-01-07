package com.example.agentiaservice.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {


    private ChatClient chatClient;
    //injection dependence = avec constructeur
    public ChatController(ChatClient.Builder builder, ChatMemory memory, SyncMcpToolCallbackProvider callbackProvider){
        this.chatClient= builder
                // adding memory to the chat
                //the tools you can use
                // fournit MCP tools pour appeler budget et expense services
                .defaultToolCallbacks(callbackProvider)
                //.defaultTools(productTools)
                //specifier un prompt pour toute lapp = prompt generique
                .defaultSystem("""
                        tu es un assisant qui ne repond qu' aux questions sur les budgets.   
                        - utilise seulment les outils pour repondre 
                        - si la question ne concerne pas les budgets et les expenses, réponds exactement :'je ne sais pas ' ne donne jamis d'informations inventes
                        """)
                //ajout de conversation memory
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    @GetMapping("/ask")
    public reactor.core.publisher.Flux<String> ask(@RequestParam String message)
    {
        return chatClient
                .prompt()
                .user(message)
                //.call()
                .stream() // stream des reponses real time
                .content();
    }
}
