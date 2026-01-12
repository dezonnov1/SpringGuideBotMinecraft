package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotAction;
import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.JvmArgument;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.dezonnov1.SpringGuideBotMinecraft.repository.JvmArgumentRepository;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText; // Используем Edit, чтобы не спамить
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JvmArgumentsDetailHandler implements DialogHandler {

    private final JvmArgumentRepository repository;

    @Override
    public boolean isApplicable(BotState currentState, Update update) {
        // Проверяем, начинается ли callbackData с нашего префикса "JVM_PAGE:"
        return currentState == BotState.MAIN_MENU
                && update.callbackQuery() != null
                && update.callbackQuery().data().startsWith(BotAction.JVM_PAGE_PREFIX);
    }

    @Override
    public HandlerResult handle(UserSession session, Update update) {
        List<BaseRequest<?, ?>> responses = new ArrayList<>();
        String callbackData = update.callbackQuery().data();

        // Отвечаем на коллбэк
        responses.add(new AnswerCallbackQuery(update.callbackQuery().id()));

        // Парсим номер страницы из строки "JVM_PAGE:5"
        int pageIndex = 0;
        try {
            String pageStr = callbackData.substring(BotAction.JVM_PAGE_PREFIX.length());
            pageIndex = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            pageIndex = 0;
        }

        // Достаем данные из БД (по 1 штуке на страницу)
        // Sort.by("id") гарантирует постоянный порядок
        Page<JvmArgument> page = repository.findAll(PageRequest.of(pageIndex, 1, Sort.by("id")));

        if (page.hasContent()) {
            JvmArgument arg = page.getContent().get(0);
            int totalPages = page.getTotalPages();
            int currentDisplayPage = pageIndex + 1; // начинаем с 1

            // Формируем текст
            String text = """
                    Аргумент:
                    ```%s```
                    
                    Описание:
                    %s
                    
                    Страница %d из %d
                    """.formatted(arg.getArgument(), arg.getDescription(), currentDisplayPage, totalPages);

            // 4. Формируем кнопки навигации
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> navRow = new ArrayList<>();

            // Кнопка "Предыдущий" (если есть куда листать назад)
            if (page.hasPrevious()) {
                navRow.add(new InlineKeyboardButton("<--<")
                        .callbackData(BotAction.JVM_PAGE_PREFIX + (pageIndex - 1)));
            }

            // Кнопка "Следующий" (если есть куда листать вперед)
            if (page.hasNext()) {
                navRow.add(new InlineKeyboardButton(">-->")
                        .callbackData(BotAction.JVM_PAGE_PREFIX + (pageIndex + 1)));
            }

            keyboard.addRow(navRow.toArray(new InlineKeyboardButton[0]));

            // Кнопка "В меню"
            keyboard.addRow(new InlineKeyboardButton("В меню")
                    .callbackData(BotAction.GO_TO_MENU.getCallbackData()));

            // 5. Редактируем старое сообщение (красивее, чем слать новое)
            // session.getChatId() и update.callbackQuery().message().messageId()
            EditMessageText editMessage = new EditMessageText(
                    session.getChatId(),
                    update.callbackQuery().message().messageId(),
                    text
            ).parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard);

            responses.add(editMessage);
        } else {
            // Если вдруг страницы нет (база пуста?)
            // Можно отправить алерт или вернуть в меню
        }

        return new HandlerResult(responses, BotState.MAIN_MENU);
    }
}