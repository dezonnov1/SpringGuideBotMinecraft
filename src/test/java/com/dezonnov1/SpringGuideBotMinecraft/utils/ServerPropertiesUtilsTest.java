package com.dezonnov1.SpringGuideBotMinecraft.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerPropertiesUtilsTest {

    @Test
    void appendFormattedComment_ShouldAddHashPrefix() {
        StringBuilder sb = new StringBuilder();
        ServerPropertiesUtils.appendFormattedComment(sb, "Тестовая строка");

        assertEquals("# Тестовая строка\n", sb.toString());
    }

    @Test
    void appendFormattedComment_ShouldSplitLongLines() {
        StringBuilder sb = new StringBuilder();
        // Создаем строку длиннее 100 символов
        String longText = "A".repeat(110);

        ServerPropertiesUtils.appendFormattedComment(sb, longText);

        // Проверяем, что вставился перенос строки (стало 2 строки комментариев)
        String result = sb.toString();
        assertTrue(result.contains("\n# "), "Длинная строка должна быть разбита");
        assertTrue(result.startsWith("# "), "Первая строка должна начинаться с #");
    }

    @Test
    void appendFormattedComment_ShouldIgnoreNullOrEmpty() {
        StringBuilder sb = new StringBuilder();
        ServerPropertiesUtils.appendFormattedComment(sb, null);
        ServerPropertiesUtils.appendFormattedComment(sb, "");

        assertTrue(sb.toString().isEmpty(), "Для пустого ввода StringBuilder должен остаться пустым");
    }
}