package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotAction;
import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Хендлер стартового диалога.
 * Отвечает за:
 * 1. Обработку команды /start
 * 2. Обработку кнопки "Вернуться в меню" (BotAction.GO_TO_MENU)
 * 3. Генерацию клавиатуры главного меню для других хендлеров.
 */
@Component
public class StartHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Вариант 1: Пользователь ввел /start
        if (update.message() != null && "/start".equals(update.message().text())) {
            return true;
        }

        // Вариант 2: Пользователь нажал кнопку "В меню"
        // Используем Enum для сравнения
        return currentState == BotState.MAIN_MENU
                && update.callbackQuery() != null
                && BotAction.GO_TO_MENU.getCallbackData().equals(update.callbackQuery().data());
    }

    /**
     * Создает клавиатуру главного меню.
     * Используется здесь и в AboutHandler (через getWelcomeMessage).
     */
    public static InlineKeyboardMarkup getMainMenuKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("📂 Server.properties")
                        .callbackData(BotAction.SHOW_SERVER_PROPS.getCallbackData()),
                new InlineKeyboardButton("ℹ️ О разработчике")
                        .callbackData(BotAction.SHOW_ABOUT.getCallbackData()),
                new InlineKeyboardButton("☕ Аргументы запуска")
                        .callbackData(BotAction.SHOW_JVM_ARGS.getCallbackData())
        );
    }

    /**
     * Генерирует приветственное сообщение.
     * Публичный метод, чтобы другие хендлеры могли "вернуть" пользователя в меню.
     */
    public SendMessage getWelcomeMessage(Long chatId) {
        String text = """
                *МЕНЮ:*
                Этот бот поможет в настройке сервера Minecraft\\.
                \\(Версии 1\\.12 и новее\\)
                
                Выберите действие ниже:
                """;

        return new SendMessage(chatId, text)
                .parseMode(ParseMode.MarkdownV2)
                .replyMarkup(getMainMenuKeyboard());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        List<BaseRequest<?, ?>> responses = new ArrayList<>();

        // Если это было нажатие кнопки, гасим индикатор загрузки
        if (update.callbackQuery() != null) {
            responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));
        }

        responses.add(getWelcomeMessage(session.getChatId()));

        return new HandlerResult(responses, BotState.MAIN_MENU);
    }
}
