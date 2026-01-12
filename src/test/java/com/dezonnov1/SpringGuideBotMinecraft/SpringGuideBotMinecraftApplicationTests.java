package com.dezonnov1.SpringGuideBotMinecraft;

import com.dezonnov1.SpringGuideBotMinecraft.handler.StartHandler;
import com.dezonnov1.SpringGuideBotMinecraft.service.DialogManager;
import com.dezonnov1.SpringGuideBotMinecraft.service.TelegramBotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest // Поднимает полный контекст приложения
@ActiveProfiles("test")
class SpringGuideBotMinecraftApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TelegramBotService botService;

    @Autowired
    private DialogManager dialogManager;

    @Autowired
    private StartHandler startHandler;

    @Test
    @DisplayName("Контекст приложения должен успешно загружаться")
    void contextLoads() {
        // Если этот метод вызвался, значит Spring успешно стартовал
        assertNotNull(context, "ApplicationContext не должен быть null");
    }

    @Test
    @DisplayName("Критические компоненты (Бины) должны быть созданы")
    void beansShouldBeLoaded() {
        assertNotNull(botService, "TelegramBotService не загрузился");
        assertNotNull(dialogManager, "DialogManager не загрузился");
        assertNotNull(startHandler, "StartHandler не загрузился");
    }
}