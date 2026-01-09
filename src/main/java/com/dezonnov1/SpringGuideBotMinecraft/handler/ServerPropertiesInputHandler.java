package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerPropertiesInputHandler implements DialogHandler {

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Реагируем на любое сообщение, если мы в состоянии ожидания
        return currentState == BotState.WAITING_FOR_SP_INPUT && update.message() != null;
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        Long chatId = session.getChatId();

        // 1. Если прислали файл
        if (update.message().document() != null) {

            // TODO: Тут будет логика скачивания и обработки файла

            SendMessage msg = new SendMessage(chatId, "Файл получен! Генерирую ответ... (заглушка)");

            // Успех -> Сбрасываем стейт в Главное меню
            return new HandlerResult(List.of(msg), BotState.MAIN_MENU);
        }

        // 2. Если прислали текст (Версию)
        if (update.message().text() != null) {
            String text = update.message().text().trim();

            // Простая регулярка для версий (1.12, 1.16.5 и т.д.)
            if (text.matches("\\d+\\.\\d+(\\.\\d+)?")) {

                // TODO: Тут логика генерации файла по версии

                SendMessage msg = new SendMessage(chatId, "Выбрана версия: " + text + ". Вот ваш файл... (заглушка)");

                // Успех -> Сбрасываем стейт в Главное меню
                return new HandlerResult(List.of(msg), BotState.MAIN_MENU);
            } else {
                // ОШИБКА ВВОДА
                SendMessage errorMsg = new SendMessage(chatId,
                        "Неверный формат версии!\n" +
                                "Пример: 1.16.5\n" +
                                "Попробуйте еще раз или отправьте файл.");

                // ВАЖНО: Возвращаем null. Состояние в БД не изменится.
                // Бот продолжит ждать ввод в этом хендлере.
                return new HandlerResult(List.of(errorMsg), null);
            }
        }

        // Если прислали стикер, голосовое и т.д.
        SendMessage unknownMsg = new SendMessage(chatId, "Я жду файл server.properties или номер версии.");
        return new HandlerResult(List.of(unknownMsg), null);
    }
}
