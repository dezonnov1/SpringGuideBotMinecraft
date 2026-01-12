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

    /** Главное меню аргументов */
    SHOW_JVM_ARGS("MENU_JVM_ARGS"),

    /** Переход подробнее об аргументах (страница)*/
    SHOW_JVM_DETAILS("JVM_DETAILS_INIT"),

    /** Кнопка "Назад в меню" */
    GO_TO_MENU("GO_TO_MENU"),

    /** Кнопка "Отмена" при вводе данных */
    CANCEL_INPUT("CANCEL_INPUT");

    /** Поле, хранящее строковое значение для Telegram */
    private final String callbackData;

    /** Префикс для пагинации */
    public static final String JVM_PAGE_PREFIX = "JVM_PAGE:";
}
