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

@Component
public class ServerPropertiesMenuHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Проверяем нажатие кнопки SHOW_SERVER_PROPS
        return currentState == BotState.MAIN_MENU
                && update.callbackQuery() != null
                && BotAction.SHOW_SERVER_PROPS.getCallbackData().equals(update.callbackQuery().data());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        List<BaseRequest<?, ?>> responses = new ArrayList<>();
        Long chatId = session.getChatId();

        // Отправляем подтверждение нажатия кнопки (чтобы убрать часики загрузки)
        responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));

        String text = """
                *Настройка Server\\.properties*
                
                Этот файл отвечает за генерацию мира и правила сервера\\.
                Бот добавит комментарии к каждому параметру\\.
                
                *Введите версию игры* \\(например: `1\\.16\\.5`\\), или отправьте свой `server.properties` чтобы получить готовый файл\\.
                """;

        SendMessage request = new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2);

        // Кнопка отмены с действием CANCEL_INPUT
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("В меню")
                        .callbackData(BotAction.CANCEL_INPUT.getCallbackData())
        );
        request.replyMarkup(keyboard);

        responses.add(request);
        // Переводим бота в состояние ожидания ввода версии
        return new HandlerResult(responses, BotState.WAITING_FOR_SP_INPUT);
    }
}
