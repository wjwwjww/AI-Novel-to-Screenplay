package com.complier.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "screenplay_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenplayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String novelText;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String screenplayText;

    private String promptStyle;

    private String modelUsed;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
