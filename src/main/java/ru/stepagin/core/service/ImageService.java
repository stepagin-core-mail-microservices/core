package ru.stepagin.core.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.stepagin.core.dto.ImageDto;
import ru.stepagin.core.entity.ImageEntity;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.exception.BadFileException;
import ru.stepagin.core.exception.ImageNotFoundException;
import ru.stepagin.core.mapper.ImageMapper;
import ru.stepagin.core.repository.ImageRepository;
import ru.stepagin.core.security.Role;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public List<ImageDto> saveImages(MultipartFile[] imgList, UserEntity userEntity) {
        List<ImageEntity> imageList = new ArrayList<>();
        for (MultipartFile file : imgList) {
            if (!Objects.equals(file.getContentType(), "image/jpeg") &&
                    !Objects.equals(file.getContentType(), "image/png")) {
                throw new BadFileException("Bad file format for image " + file.getOriginalFilename());
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new BadFileException("Too large image size for image " + file.getOriginalFilename());
            }
            imageList.add(createEntity(file, userEntity));
        }

        return ImageMapper.toDto(imageRepository.saveAll(imageList));
    }

    private ImageEntity createEntity(MultipartFile img, UserEntity owner) {
        ImageEntity imageEntity = new ImageEntity();
        try {
            imageEntity.setContentType(img.getContentType());
            imageEntity.setSize(img.getSize());
            imageEntity.setBytes(img.getBytes());
            imageEntity.setName(img.getOriginalFilename());
            imageEntity.setOwner(owner);
            imageEntity.setCreationDate(LocalDateTime.now());
        } catch (IOException e) {
            throw new RuntimeException("Error while reading image " + img.getOriginalFilename());
        }
        return imageEntity;
    }

    public List<ImageDto> getAllByOwner(String owner, int page, int limit, String sort,
                                        String idFilter, String sizeFilter, String dateFilter) {
        List<Sort.Order> orders = getOrders(sort);
        Sort sorting = Sort.by(orders);
        PageRequest pageRequest = PageRequest.of(page, limit, sorting);
        LocalDateTime[] dateFilters = parseDateFilters(dateFilter);
        Long[] sizeFilters = parseSizeFilters(sizeFilter);
        if (idFilter == null) {
            idFilter = "";
        }
        if (owner == null) {
            return ImageMapper.toDto(imageRepository.findByFilters(
                    dateFilters[0],
                    dateFilters[1],
                    sizeFilters[0],
                    sizeFilters[1],
                    idFilter,
                    pageRequest));

        }
        return ImageMapper.toDto(imageRepository.findByOwnerAndFilters(
                owner,
                dateFilters[0],
                dateFilters[1],
                sizeFilters[0],
                sizeFilters[1],
                idFilter,
                pageRequest));
    }

    private LocalDateTime[] parseDateFilters(String dateFilter) {
        LocalDateTime dateFilterFrom = LocalDateTime.of(1, 1, 1, 0, 0);
        LocalDateTime dateFilterTo = LocalDateTime.of(9999, 1, 1, 0, 0);
        if (dateFilter != null) {
            try {
                String[] dateFilterParts = dateFilter.split(",", -1);
                if (dateFilterParts.length == 2) {
                    if (dateFilterParts[0] != null && !dateFilterParts[0].isEmpty())
                        dateFilterFrom = LocalDateTime.parse(dateFilterParts[0], ImageMapper.formatter);
                    if (dateFilterParts[1] != null && !dateFilterParts[1].isEmpty())
                        dateFilterTo = LocalDateTime.parse(dateFilterParts[1], ImageMapper.formatter);
                } else if (dateFilterParts.length == 1) {
                    if (dateFilterParts[0] != null && !dateFilterParts[0].isEmpty())
                        dateFilterFrom = LocalDateTime.parse(dateFilterParts[0], ImageMapper.formatter);
                    dateFilterTo = dateFilterFrom;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while parsing data filter");
            }
        }
        return new LocalDateTime[]{dateFilterFrom, dateFilterTo.plusSeconds(1)};
    }

    public List<ImageDto> getAll(UserEntity owner, int page, int limit, String sort,
                                 String idFilter, String sizeFilter, String dateFilter) {
        if (owner.getRoles().contains(Role.MODERATOR)) {
            return getAllByOwner(null, page, limit, sort, idFilter, sizeFilter, dateFilter);
        }
        return getAllByOwner(owner.getLogin(), page, limit, sort, idFilter, sizeFilter, dateFilter);
    }

    private List<Sort.Order> getOrders(String sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            for (String sortCriteria : sort.split(",", -1)) {
                if (sortCriteria == null || sortCriteria.isEmpty()) {
                    throw new IllegalArgumentException("Empty sort criteria is not allowed");
                }
                String fieldName = sortCriteria.substring(1);
                if (!(fieldName.equals("creationDate") || fieldName.equals("id") || fieldName.equals("size")))
                    throw new IllegalArgumentException("Invalid sort criteria: " + fieldName);
                if (sortCriteria.startsWith("+")) {
                    orders.add(Sort.Order.asc(fieldName));
                } else if (sortCriteria.startsWith("-")) {
                    orders.add(Sort.Order.desc(fieldName));
                }
            }
        }
        return orders;
    }

    private Long[] parseSizeFilters(String sizeFilter) {
        Long sizeFilterFrom = 0L;
        Long sizeFilterTo = Long.MAX_VALUE;
        if (sizeFilter != null) {
            try {
                String[] sizeFilterParts = sizeFilter.split(",", -1);
                if (sizeFilterParts.length == 2) {
                    if (sizeFilterParts[0] != null && !sizeFilterParts[0].isEmpty())
                        sizeFilterFrom = Long.parseLong(sizeFilterParts[0]);
                    if (sizeFilterParts[1] != null && !sizeFilterParts[1].isEmpty())
                        sizeFilterTo = Long.parseLong(sizeFilterParts[1]);
                } else if (sizeFilterParts.length == 1) {
                    if (sizeFilterParts[0] != null && !sizeFilterParts[0].isEmpty()) {
                        sizeFilterFrom = Long.parseLong(sizeFilterParts[0]);
                        sizeFilterTo = sizeFilterFrom;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while parsing size filter");
            }
        }
        return new Long[]{sizeFilterFrom, sizeFilterTo};
    }

    public ImageEntity getImageEntity(String uuid, UserEntity user) {
        ImageEntity image = imageRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new ImageNotFoundException(uuid));
        if (!Objects.equals(image.getOwner().getLogin(), user.getLogin())) {
            throw new ImageNotFoundException(uuid);
        }
        return image;
    }
}
