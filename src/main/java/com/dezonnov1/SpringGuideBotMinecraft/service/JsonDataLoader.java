package com.dezonnov1.SpringGuideBotMinecraft.service;

import com.dezonnov1.SpringGuideBotMinecraft.dto.JvmArgumentJsonDto;
import com.dezonnov1.SpringGuideBotMinecraft.dto.PropertyJsonDto;
import com.dezonnov1.SpringGuideBotMinecraft.entity.JvmArgument;
import com.dezonnov1.SpringGuideBotMinecraft.entity.MinecraftVersion;
import com.dezonnov1.SpringGuideBotMinecraft.entity.ServerProperty;
import com.dezonnov1.SpringGuideBotMinecraft.repository.JvmArgumentRepository;
import com.dezonnov1.SpringGuideBotMinecraft.repository.MinecraftVersionRepository;
import com.dezonnov1.SpringGuideBotMinecraft.repository.ServerPropertyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonDataLoader implements CommandLineRunner {

    private final ServerPropertyRepository propertyRepository;
    private final MinecraftVersionRepository versionRepository;
    private final JvmArgumentRepository jvmArgumentRepository; // Не забудьте добавить это поле!
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadProperties();
        loadJvmArguments();
    }

    /**
     * Метод загрузки server.properties
     */
    private void loadProperties() {
        if (propertyRepository.count() > 0) {
            log.info("База данных server.properties не пуста. Пропуск загрузки.");
            return;
        }

        log.info("Загрузка данных из server_properties.json...");

        try (InputStream inputStream = getClass().getResourceAsStream("/server_properties.json")) {
            if (inputStream == null) {
                log.error("Файл server_properties.json не найден в resources!");
                return;
            }

            List<PropertyJsonDto> dtos = objectMapper.readValue(inputStream, new TypeReference<>() {});

            for (PropertyJsonDto dto : dtos) {
                savePropertyFromDto(dto);
            }

            log.info("Успешно загружено {} параметров server.properties.", dtos.size());
        } catch (Exception e) {
            log.error("Ошибка загрузки server_properties.json: ", e);
        }
    }

    /**
     * Метод загрузки JVM аргументов
     */
    private void loadJvmArguments() {
        if (jvmArgumentRepository.count() > 0) {
            log.info("База данных JVM аргументов не пуста. Пропуск загрузки.");
            return;
        }

        log.info("Загрузка данных из jvm_arguments.json...");

        try (InputStream inputStream = getClass().getResourceAsStream("/jvm_arguments.json")) {
            if (inputStream == null) {
                log.warn("Файл jvm_arguments.json не найден (возможно, вы его еще не создали).");
                return;
            }

            List<JvmArgumentJsonDto> dtos = objectMapper.readValue(inputStream, new TypeReference<>() {});

            List<JvmArgument> arguments = dtos.stream()
                    .map(dto -> JvmArgument.builder()
                            .argument(dto.getArgument())
                            .description(dto.getDescription())
                            .build())
                    .toList();

            jvmArgumentRepository.saveAll(arguments);
            log.info("Успешно загружено {} JVM аргументов.", arguments.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки jvm_arguments.json: ", e);
        }
    }

    // Вспомогательный метод сохранения (остался без изменений)
    private void savePropertyFromDto(PropertyJsonDto dto) {
        Set<MinecraftVersion> versionsForThisProperty = new HashSet<>();

        if (dto.getVersions() != null) {
            for (String vName : dto.getVersions()) {
                MinecraftVersion version = versionRepository.findByVersionName(vName)
                        .orElseGet(() -> versionRepository.save(
                                MinecraftVersion.builder().versionName(vName).build()
                        ));
                versionsForThisProperty.add(version);
            }
        }

        ServerProperty property = ServerProperty.builder()
                .parameter(dto.getParameter())
                .defaultValue(dto.getDefaultValue())     // Берется из "on_default"
                .possibleValues(dto.getPossibleValues()) // Берется из "values"
                .description(dto.getDescription())
                .recommendation(dto.getRecommendation())
                .versions(versionsForThisProperty)       // Связь Many-to-Many
                .build();

        propertyRepository.save(property);
    }
}