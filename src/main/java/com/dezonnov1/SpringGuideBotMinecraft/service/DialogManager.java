package com.dezonnov1.SpringGuideBotMinecraft.service;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.dezonnov1.SpringGuideBotMinecraft.handler.DialogHandler;
import com.dezonnov1.SpringGuideBotMinecraft.handler.HandlerResult;
import com.dezonnov1.SpringGuideBotMinecraft.repository.UserSessionRepository;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class DialogManager {

    private final List<DialogHandler> handlers;
    private final UserSessionRepository sessionRepository;

    @Transactional
    public List<BaseRequest<?, ?>> processUpdate(Update update) {
        // Если пришло не сообщение и не коллбэк от кнопки
        if (update.message() == null && update.callbackQuery() == null) {
            return Collections.emptyList();
        }

        // Получаем ID чата в зависимости от типа обновления
        Long chatId = update.message() != null ? update.message().chat().id() : update.callbackQuery().message().chat().id();

        // Получаем или создаем сессию
        UserSession session = sessionRepository.findById(chatId)
                .orElseGet(() -> createNewSession(chatId));

        // Ищем подходящий хендлер
        DialogHandler handler = handlers.stream()
                .filter(h -> h.isApplicable(session.getState(), update))
                .findFirst()
                .orElse(null);

        if (handler == null) {
            log.debug("Получено сообщение, которое не имеет нужного handler ChatID {}, сообщение {}",chatId ,update.message().text());
            // Можно вернуть сообщение "Я вас не понимаю" или сбросить в начало
            return Collections.emptyList();
        }

        HandlerResult result = handler.handle(session, update);

        // Если хендлер вернул новый стейт (не null), обновляем БД
        if (result.nextState() != null) {
            session.setState(result.nextState());
            session.setUpdatedAt(LocalDateTime.now());
            sessionRepository.save(session);
        }

        return result.requests();
    }

    private UserSession createNewSession(Long chatId) {
        UserSession session = UserSession.builder()
                .chatId(chatId)
                .state(BotState.START)
                .updatedAt(LocalDateTime.now())
                .build();
        return sessionRepository.save(session);
    }
}
