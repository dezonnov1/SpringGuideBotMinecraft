package com.dezonnov1.SpringGuideBotMinecraft.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetMe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private TelegramBot bot;
    private final DialogManager dialogManager;

    @PostConstruct
    public void init() {
        bot = new TelegramBot(botToken);
        log.info("Бот запущен! @{}", bot.execute(new GetMe()).user().username());

        bot.setUpdatesListener(updates -> {
            log.debug("Получено {} обновлений",updates.size());
            for (Update update : updates) {
                try {
                    List<BaseRequest<?, ?>> responses = dialogManager.processUpdate(update);
                    // Отправляем ответы
                    for (BaseRequest<?, ?> response : responses) {
                        bot.execute(response);
                    }
                } catch (Exception e) {
                    log.trace(Marker.ANY_MARKER, "Ошибка в сервисе ТГ бота",e);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}