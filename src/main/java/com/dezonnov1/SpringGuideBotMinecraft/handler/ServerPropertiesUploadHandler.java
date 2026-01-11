package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.ServerProperty;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.dezonnov1.SpringGuideBotMinecraft.repository.ServerPropertyRepository;
import com.dezonnov1.SpringGuideBotMinecraft.service.BotInfoHolder;
import com.dezonnov1.SpringGuideBotMinecraft.utils.ServerPropertiesUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerPropertiesUploadHandler implements DialogHandler {

    private final ServerPropertyRepository propertyRepository;
    private final BotInfoHolder botInfoHolder;
    @Lazy
    private final StartHandler startHandler;

    private final TelegramBot telegramBot;

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Срабатываем, если прислан ДОКУМЕНТ и его имя заканчивается на .properties
        if (update.message() != null && update.message().document() != null) {
            String fileName = update.message().document().fileName();
            return fileName != null && fileName.endsWith(".properties");
        }
        return false;
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        Long chatId = session.getChatId();
        Document document = update.message().document();
        List<BaseRequest<?, ?>> responses = new ArrayList<>();

        // Сообщение "Ждите..."
        responses.add(new SendMessage(chatId, "Скачиваю и анализирую файл..."));

        try {
            // 1. Получаем путь к файлу на серверах Telegram
            GetFile request = new GetFile(document.fileId());
            GetFileResponse getFileResponse = telegramBot.execute(request);

            if (getFileResponse.isOk()) {
                File file = getFileResponse.file();

                // 2. Формируем URL для скачивания
                String fullPath = telegramBot.getFullFilePath(file);

                // 3. Скачиваем и обрабатываем контент
                String processedContent = processPropertiesFile(fullPath);

                // 4. Отправляем результат
                byte[] fileBytes = processedContent.getBytes(StandardCharsets.UTF_8);
                SendDocument sendDoc = new SendDocument(chatId, fileBytes)
                        .fileName("server.properties")
                        .caption("Ваш файл обработан! Добавлены описания ко всем известным параметрам.");

                responses.add(sendDoc);
                responses.add(startHandler.getWelcomeMessage(chatId));
            } else {
                responses.add(new SendMessage(chatId, "Не удалось скачать файл. Ошибка API."));
                responses.add(startHandler.getWelcomeMessage(chatId));
            }
        } catch (Exception e) {
            log.error("Произошла ошибка при обработке файла:", e);
            responses.add(new SendMessage(chatId, "Произошла ошибка при обработке файла: " + e.getMessage()));
            responses.add(startHandler.getWelcomeMessage(chatId));
        }

        // Остаемся в том же состоянии (или возвращаем в меню)
        return new HandlerResult(responses, BotState.MAIN_MENU);
    }

    /**
     * Основная логика парсинга и добавления комментариев
     */
    private String processPropertiesFile(String fileUrl) throws IOException {
        StringBuilder result = new StringBuilder();

        result.append("# Обработано ботом ").append(botInfoHolder.getBotLink()).append("\n");
        result.append("# (Оригинальные комментарии были удалены для чистоты)\n\n");

        try (InputStream in = new URL(fileUrl).openStream();
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    result.append("\n");
                    continue;
                }

                if (line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }

                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();

                    Optional<ServerProperty> propOpt = propertyRepository.findFirstByParameter(key);

                    if (propOpt.isPresent()) {
                        ServerProperty prop = propOpt.get();

                        ServerPropertiesUtils.appendFormattedComment(result, prop.getDescription());

                        if (prop.getRecommendation() != null && !prop.getRecommendation().isEmpty()) {
                            ServerPropertiesUtils.appendFormattedComment(result, "Рекомендация: " + prop.getRecommendation());
                        }
                    }
                }

                result.append(line).append("\n\n");
            }
        }
        return result.toString();
    }
}
