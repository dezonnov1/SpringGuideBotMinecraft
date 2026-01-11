package com.dezonnov1.SpringGuideBotMinecraft.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "minecraft_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinecraftVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long id;

    @Column(name = "version_name", nullable = false, unique = true)
    private String versionName;

    @ManyToMany(mappedBy = "versions")
    private Set<ServerProperty> properties;
}
