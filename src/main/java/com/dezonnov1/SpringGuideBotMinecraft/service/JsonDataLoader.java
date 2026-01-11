package com.dezonnov1.SpringGuideBotMinecraft.service;

import com.dezonnov1.SpringGuideBotMinecraft.dto.PropertyJsonDto;
import com.dezonnov1.SpringGuideBotMinecraft.entity.MinecraftVersion;
import com.dezonnov1.SpringGuideBotMinecraft.entity.ServerProperty;
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
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Если база не пустая - не загружаем
        if (propertyRepository.count() > 0) {
            log.info("База данных не пуста. Пропуск загрузки JSON.");
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

            log.info("Успешно загружено {} параметров.", dtos.size());
        } catch (Exception e) {
            log.error("Ошибка загрузки JSON: ", e);
        }
    }

    private void savePropertyFromDto(PropertyJsonDto dto) {
        Set<MinecraftVersion> versionsForThisProperty = new HashSet<>();

        // 1. Проходим по всем версиям из JSON и ищем/создаем их в БД
        if (dto.getVersions() != null) {
            for (String vName : dto.getVersions()) {
                MinecraftVersion version = versionRepository.findByVersionName(vName)
                        .orElseGet(() -> versionRepository.save(
                                MinecraftVersion.builder().versionName(vName).build()
                        ));
                versionsForThisProperty.add(version);
            }
        }

        // 2. Создаем параметр
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
