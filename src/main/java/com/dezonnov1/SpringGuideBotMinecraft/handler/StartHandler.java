package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        return update.message() != null && "/start".equals(update.message().text());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        String text = "МЕНЮ:\n" +
                "Этот бот поможет в настройке сервера minecraft.\n" +
                "Начиная с версии 1.12 (включительно) и новее.\n" +
                "Информация затрагивает только релизные версии игры.";

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(
                new String[]{"О разработчике", "Server.properties"})
                .resizeKeyboard(true);

        SendMessage request = new SendMessage(session.getChatId(), text).replyMarkup(keyboard);

        // Переходим в MAIN_MENU
        return new HandlerResult(List.of(request), BotState.MAIN_MENU);
    }
}