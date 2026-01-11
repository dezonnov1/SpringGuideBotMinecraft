package com.dezonnov1.SpringGuideBotMinecraft.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление всех действий (Callback Data), которые могут быть вызваны нажатием кнопок.
 */
@Getter
@RequiredArgsConstructor
public enum BotAction {

    /** Кнопка "Server.properties" */
    SHOW_SERVER_PROPS("MENU_SERVER_PROPS"),

    /** Кнопка "О разработчике" */
    SHOW_ABOUT("MENU_ABOUT"),

    /** Кнопка "Назад в меню" */
    GO_TO_MENU("GO_TO_MENU"),

    /** Кнопка "Отмена" при вводе данных */
    CANCEL_INPUT("CANCEL_INPUT");

    /** Поле, хранящее строковое значение для Telegram */
    private final String callbackData;
}
