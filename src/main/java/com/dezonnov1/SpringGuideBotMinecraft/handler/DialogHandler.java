package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.Update;

public interface DialogHandler {
    boolean isApplicable(BotState currentState, Update update);
    HandlerResult handle(UserSession session, Update update);
}