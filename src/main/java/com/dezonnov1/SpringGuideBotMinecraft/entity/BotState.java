package com.dezonnov1.SpringGuideBotMinecraft.entity;

public enum BotState {
    START,                  // Самое начало
    MAIN_MENU,              // Пользователь в главном меню
    WAITING_FOR_SP_INPUT    // Ждем файл или версию для Server.properties
}
