package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.ServerProperty;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.dezonnov1.SpringGuideBotMinecraft.repository.ServerPropertyRepository;
import com.dezonnov1.SpringGuideBotMinecraft.service.BotInfoHolder;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerPropertiesInputHandlerTest {

    @Mock private ServerPropertyRepository propertyRepository;
    @Mock private BotInfoHolder botInfoHolder;
    @Mock private StartHandler startHandler;

    @Mock private Update update;
    @Mock private Message message;
    @Mock private Document document;

    @InjectMocks
    private ServerPropertiesInputHandler inputHandler;

    @Test
    void isApplicable_ShouldReturnFalse_IfDocumentIsPresent() {
        when(update.message()).thenReturn(message);
        when(message.document()).thenReturn(document);

        boolean result = inputHandler.isApplicable(BotState.WAITING_FOR_SP_INPUT, update);

        assertFalse(result, "Хендлер должен игнорировать документы");
    }

    @Test
    void handle_ShouldGenerateFile_WhenVersionExists() {
        String version = "1.16.5";
        UserSession session = UserSession.builder().chatId(777L).build();

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(version);
        when(botInfoHolder.getBotLink()).thenReturn("@TestBot");

        ServerProperty prop = ServerProperty.builder()
                .parameter("server-port")
                .defaultValue("25565")
                .description("Port")
                .build();
        when(propertyRepository.findAllByVersionName(version)).thenReturn(List.of(prop));
        when(startHandler.getWelcomeMessage(777L)).thenReturn(new SendMessage(777L, "Menu"));

        HandlerResult result = inputHandler.handle(session, update);

        // ИСПРАВЛЕНИЕ: nextState() и requests()
        assertEquals(BotState.MAIN_MENU, result.nextState());
        assertEquals(2, result.requests().size());
        assertInstanceOf(SendDocument.class, result.requests().getFirst());
    }

    @Test
    void handle_ShouldReturnError_WhenVersionNotFound() {
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("invalid_version");

        when(propertyRepository.findAllByVersionName(anyString())).thenReturn(Collections.emptyList());

        HandlerResult result = inputHandler.handle(UserSession.builder().chatId(1L).build(), update);

        assertNull(result.nextState());
        assertInstanceOf(SendMessage.class, result.requests().getFirst());

        SendMessage msg = (SendMessage) result.requests().getFirst();
        String text = (String) msg.getParameters().get("text");
        assertTrue(text.contains("Для версии invalid_version у меня нет данных"));
    }
}