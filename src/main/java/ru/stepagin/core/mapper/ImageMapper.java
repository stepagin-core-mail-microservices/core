package ru.stepagin.core.mapper;

import ru.stepagin.core.dto.ImageDto;
import ru.stepagin.core.entity.ImageEntity;

import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class ImageMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ImageDto toDto(ImageEntity image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId().toString());
        dto.setName(image.getName());
        dto.setSize(image.getSize());
        dto.setSize(image.getSize());
        dto.setCreated(formatter.format(image.getCreationDate()));
        return dto;
    }

    public static List<ImageDto> toDto(List<ImageEntity> images) {
        return images.stream().map(ImageMapper::toDto).toList();
    }
}
