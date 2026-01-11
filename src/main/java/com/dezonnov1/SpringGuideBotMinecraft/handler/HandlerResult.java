package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.pengrad.telegrambot.request.BaseRequest;
import java.util.List;

// nextState = null означает остаться в текущем состоянии
public record HandlerResult(List<BaseRequest<?, ?>> requests, BotState nextState) {
}
