package com.dezonnov1.SpringGuideBotMinecraft.entity;

/**
 * Перечисление всех Состояний бота.
 */
public enum BotState {
    /** Самое начало */
    START,

    /** Пользователь в главном меню */
    MAIN_MENU,

    /** Ждем файл или версию для Server.properties */
    WAITING_FOR_SP_INPUT
}
