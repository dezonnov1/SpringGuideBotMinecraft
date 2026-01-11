package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotAction;
import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Хендлер раздела "О разработчике".
 */
@Component
@RequiredArgsConstructor
public class AboutHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Проверяем нажатие кнопки SHOW_ABOUT
        return currentState == BotState.MAIN_MENU
                && update.callbackQuery() != null
                && BotAction.SHOW_ABOUT.getCallbackData().equals(update.callbackQuery().data());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        List<BaseRequest<?, ?>> responses = new ArrayList<>();
        Long chatId = session.getChatId();

        responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));

        // Text Block с MarkdownV2 (не забываем экранировать спецсимволы!)
        String text = """
                *О разработчике*
                Разработчик: Студент 3-го курса ИТСС Кузнецов Д.М.
                Этот бот создан для помощи администраторам серверов Minecraft\\.
                Проект написан на *Java \\(Spring Boot\\)*\\.
                
                Технологии:
                    \\- Java 17
                    \\- Spring Boot 3
                    \\- PostgreSQL \\+ Flyway
                    \\- Docker
                
                *Связь:* [@denchik\\_slzit](https://t.me/denchik_slzit)
                """;

        LinkPreviewOptions previewOptions = new LinkPreviewOptions().isDisabled(true);

        SendMessage message = new SendMessage(chatId, text)
                .parseMode(ParseMode.MarkdownV2)
                .linkPreviewOptions(previewOptions);

        // Кнопка "Назад" ведет на BotAction.GO_TO_MENU
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("Назад в меню")
                        .callbackData(BotAction.GO_TO_MENU.getCallbackData())
        );
        message.replyMarkup(keyboard);

        responses.add(message);

        return new HandlerResult(responses, BotState.MAIN_MENU);
    }
}