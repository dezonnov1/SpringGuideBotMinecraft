package com.dezonnov1.SpringGuideBotMinecraft.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jvm_arguments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JvmArgument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String argument;

    @Column(columnDefinition = "TEXT")
    private String description;
}