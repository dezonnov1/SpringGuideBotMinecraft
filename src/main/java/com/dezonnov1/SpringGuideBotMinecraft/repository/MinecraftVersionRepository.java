package com.dezonnov1.SpringGuideBotMinecraft.repository;

import com.dezonnov1.SpringGuideBotMinecraft.entity.MinecraftVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MinecraftVersionRepository extends JpaRepository<MinecraftVersion, Long> {
    Optional<MinecraftVersion> findByVersionName(String versionName);
}
