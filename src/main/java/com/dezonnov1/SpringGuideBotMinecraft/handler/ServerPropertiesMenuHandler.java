package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerPropertiesMenuHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        return currentState == BotState.MAIN_MENU
                && update.message() != null
                && "Server.properties".equals(update.message().text());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        String text = "Настройка Server.properties:\n" +
                "Бот добавит комментарии, что делает каждый параметр.\n\n" +
                "Введите версию игры (прим 1.16.5) или отправьте свой файл server.properties";

        // Можно добавить кнопку "Отмена", если захочешь
        SendMessage request = new SendMessage(session.getChatId(), text);

        // Переходим в режим ожидания ввода
        return new HandlerResult(List.of(request), BotState.WAITING_FOR_SP_INPUT);
    }
}