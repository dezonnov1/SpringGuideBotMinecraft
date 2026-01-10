package com.dezonnov1.SpringGuideBotMinecraft.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BotInfoHolder {
    private String botUsername = "unknown_bot";
    private String botFirstName = "Bot";

    // метод для получения ссылки
    public String getBotLink() {
        return "t.me/" + botUsername;
    }
}