package com.dezonnov1.SpringGuideBotMinecraft.utils;

public class ServerPropertiesUtils {

    private static final int MAX_LINE_LENGTH = 100;

    /**
     * Форматирует текст комментария, разбивая длинные строки и добавляя #
     */
    public static void appendFormattedComment(StringBuilder sb, String text) {
        if (text == null || text.isBlank()) return;

        String[] lines = text.replace("\r", "").split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.length() <= MAX_LINE_LENGTH) {
                sb.append("# ").append(line).append("\n");
            } else {
                String[] words = line.split("\\s+");
                StringBuilder currentLine = new StringBuilder("# ");
                for (String word : words) {
                    if (currentLine.length() + word.length() + 1 > MAX_LINE_LENGTH) {
                        sb.append(currentLine).append("\n");
                        currentLine = new StringBuilder("# ").append(word);
                    } else {
                        if (currentLine.length() > 2) currentLine.append(" ");
                        currentLine.append(word);
                    }
                }
                sb.append(currentLine).append("\n");
            }
        }
    }
}