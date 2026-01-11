package com.dezonnov1.SpringGuideBotMinecraft.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final DialogManager dialogManager;
    private final TelegramBot bot;
    private final BotInfoHolder botInfoHolder;


    @PostConstruct
    public void init() {
        // удаляем старый вебхук
        // Если этого не сделать, getUpdates может не приходить
        bot.execute(new DeleteWebhook());
        log.info("Старый Webhook удален");

        GetMeResponse botInfoResponse = bot.execute(new GetMe());

        if (botInfoResponse.isOk()) {
            botInfoHolder.setBotUsername(botInfoResponse.user().username());
            botInfoHolder.setBotFirstName(botInfoResponse.user().firstName());
            log.info("Бот запущен: {} (@{})", botInfoHolder.getBotFirstName(),
                    botInfoHolder.getBotUsername());
        } else {
            log.error("Не удалось получить информацию о боте: {}", botInfoResponse.description());
        }

        // 3. Регистрируем слушатель
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    // Логируем, что пришло сообщение
                    if (update.message() != null) {
                        log.info("Получено сообщение от {}: {}",
                                update.message().from().username(),
                                update.message().text());
                    } else if (update.callbackQuery() != null) {
                        log.info("Получена кнопка от {}", update.callbackQuery().from().username());
                    }

                    // Обрабатываем через менеджер
                    List<BaseRequest<?, ?>> responses = dialogManager.processUpdate(update);

                    // Отправляем ответы
                    for (BaseRequest<?, ?> request : responses) {
                        BaseResponse response = bot.execute(request);
                        if (!response.isOk()) {
                            log.error("Ошибка отправки сообщения Telegram: {} - {}", response.errorCode(), response.description());
                        }
                    }
                } catch (Exception e) {
                    // Если ошибка случилась в нашей логике - выводим стек
                    log.error("КРИТИЧЕСКАЯ ОШИБКА ПРИ ОБРАБОТКЕ UPDATE: ", e);
                }
            }
            // Всегда подтверждаем получение, чтобы не зациклить ошибку
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            // Ошибка связи с серверами Телеграм
            log.error("Ошибка сети Telegram API: ", e);
        });
    }
}