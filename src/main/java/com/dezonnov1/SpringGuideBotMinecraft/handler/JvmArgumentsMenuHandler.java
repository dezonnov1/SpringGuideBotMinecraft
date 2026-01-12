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
public class JvmArgumentsMenuHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        return currentState == BotState.MAIN_MENU
                && update.callbackQuery() != null
                && BotAction.SHOW_JVM_ARGS.getCallbackData().equals(update.callbackQuery().data());
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        List<BaseRequest<?, ?>> responses = new ArrayList<>();
        responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));

        String text = """
                Аргументы запуска \\(JVM Flags\\)
                
                Правильные флаги помогают серверу работать стабильнее и использовать ресурсы эффективнее\\.
                
                Рекомендуемая строка запуска\\:
                ```
                java -Xincgc -Xms512M -Xmx4G -XX:MaxPermSize=128M -XX:SharedReadOnlySize=30M -XX:+UseConcMarkSweepGC -XX:+UseBiasedLocking -XX:+UseFastAccessorMethods -XX:+UseCompressedOops -jar server.jar nogui
                ```
                
                \\(Нажмите на текст команды, чтобы скопировать\\)
                
                Вы можете узнать подробнее о значении каждого флага, нажав кнопку ниже\\.
                """;

        SendMessage message = new SendMessage(session.getChatId(), text).parseMode(ParseMode.MarkdownV2);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                // Кнопка перехода к деталям (начинаем с 0 страницы)
                new InlineKeyboardButton("Подробнее об аргументах")
                        .callbackData(BotAction.JVM_PAGE_PREFIX + "0")
        );

        keyboard.addRow(new InlineKeyboardButton("В меню")
                .callbackData(BotAction.GO_TO_MENU.getCallbackData()));

        message.replyMarkup(keyboard);
        responses.add(message);

        return new HandlerResult(responses, BotState.MAIN_MENU);
    }
}