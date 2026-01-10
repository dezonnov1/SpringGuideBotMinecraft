package com.dezonnov1.SpringGuideBotMinecraft.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "server_properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String parameter; // например "enable-command-block"

    @Column(name = "possible_values", columnDefinition = "TEXT")
    private String possibleValues;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    // Самое важное: Настройка связи Many-to-Many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "server_property_versions",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "version_id")
    )
    private Set<MinecraftVersion> versions;
}