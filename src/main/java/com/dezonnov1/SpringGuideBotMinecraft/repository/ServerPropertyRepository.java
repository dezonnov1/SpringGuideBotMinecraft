package com.dezonnov1.SpringGuideBotMinecraft.repository;

import com.dezonnov1.SpringGuideBotMinecraft.entity.ServerProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerPropertyRepository extends JpaRepository<ServerProperty, Long> {

    // запрос найти свойства, у которых в списке versions есть версия с именем: versionName
    @Query("SELECT p FROM ServerProperty p JOIN p.versions v WHERE v.versionName = :versionName")
    List<ServerProperty> findAllByVersionName(String versionName);

    // Проверка, существует ли такая версия вообще (чтобы не генерировать пустой файл)
    boolean existsByVersions_VersionName(String versionName);
}
