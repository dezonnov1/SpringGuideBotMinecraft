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

        responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));

        String text = """
                <b>⚙️ Настройка Server.properties</b>
                
                Этот файл отвечает за генерацию мира и правила сервера.
                Бот добавит комментарии к каждому параметру.
                
                ✍️ <b>Введите версию игры</b> (например: <code>1.16.5</code>), чтобы получить готовый файл.
                """;

        SendMessage request = new SendMessage(chatId, text).parseMode(ParseMode.HTML);

        // Кнопка отмены с действием CANCEL_INPUT
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("🔙 Отмена / В меню")
                        .callbackData(BotAction.CANCEL_INPUT.getCallbackData())
        );
        request.replyMarkup(keyboard);

        responses.add(request);

        return new HandlerResult(responses, BotState.WAITING_FOR_SP_INPUT);
    }
}