package ru.stepagin.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", nullable = false)
    private UUID id;

    @Column(name = "originalFileName")
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "contentType", nullable = false)
    private String contentType;

    @CreatedDate
    @Column(name = "date", nullable = false)
    private LocalDateTime creationDate;

    @Lob
    @Column(name = "bytes", nullable = false)
    private byte[] bytes;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private UserEntity owner;
}
