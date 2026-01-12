package com.dezonnov1.SpringGuideBotMinecraft.handler;

import com.dezonnov1.SpringGuideBotMinecraft.entity.BotState;
import com.dezonnov1.SpringGuideBotMinecraft.entity.UserSession;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartHandlerTest {

    @InjectMocks
    private StartHandler startHandler;

    @Mock
    private Update update;
    @Mock
    private Message message;
    @Mock
    private CallbackQuery callbackQuery;

    @Test
    void isApplicable_ShouldReturnTrue_WhenCommandIsStart() {
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");

        boolean result = startHandler.isApplicable(BotState.MAIN_MENU, update);
        assertTrue(result);
    }

    @Test
    void handle_ShouldReturnWelcomeMessageAndMenuState() {
        UserSession session = UserSession.builder().chatId(123L).build();

        HandlerResult result = startHandler.handle(session, update);

        assertNotNull(result);

        assertEquals(BotState.MAIN_MENU, result.nextState());
        assertFalse(result.requests().isEmpty());
        assertInstanceOf(SendMessage.class, result.requests().getFirst());
    }
}