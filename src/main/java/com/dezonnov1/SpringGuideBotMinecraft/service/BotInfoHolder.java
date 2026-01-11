package com.dezonnov1.SpringGuideBotMinecraft.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Сервис для получения данных о боте
 * Используется для вставки ссылки ведущего на бота.
 * Все данные получаем благодаря Токену, указанному в переменной среды
 */

@Component
@Getter
@Setter
public class BotInfoHolder {
    private String botUsername = "unknown_bot";
    private String botFirstName = "Bot";

    public String getBotLink() {
        return "t.me/" + botUsername;
    }
}
