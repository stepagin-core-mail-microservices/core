package ru.stepagin.core.controller;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.stepagin.core.dto.ImageDto;
import ru.stepagin.core.entity.ImageEntity;
import ru.stepagin.core.entity.UserEntity;
import ru.stepagin.core.security.SecurityService;
import ru.stepagin.core.service.ImageService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("${api.endpoints.base-url}/images")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final SecurityService securityService;

    @PostMapping
    @PreAuthorize("!@securityService.isBlocked(authentication)")
    public ResponseEntity<List<ImageDto>> uploadImages(@NotNull @RequestParam("img") MultipartFile[] imgList,
                                                       Authentication auth) {
        UserEntity owner = securityService.getUserEntity(auth);
        return ResponseEntity.ok(imageService.saveImages(imgList, owner));
    }

    @GetMapping
    @PreAuthorize("!@securityService.isBlocked(authentication)")
    public ResponseEntity<List<ImageDto>> getAllImages(
            Authentication auth,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(value = "id", required = false) String idFilter,
            @RequestParam(value = "size", required = false) String sizeFilter,
            @RequestParam(value = "date", required = false) String dateFilter
    ) {
        UserEntity owner = securityService.getUserEntity(auth);
        return ResponseEntity.ok(imageService.getAll(owner, page, limit, sort, idFilter, sizeFilter, dateFilter));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("!@securityService.isBlocked(authentication)")
    public ResponseEntity<byte[]> downloadImageByUuid(
            @PathVariable String uuid,
            Authentication auth
    ) {
        UserEntity user = securityService.getUserEntity(auth);
        ImageEntity image = imageService.downloadImage(uuid, user);
        byte[] imageData = image.getBytes();
        String fileName = URLEncoder.encode(image.getName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.valueOf(image.getContentType()))
                .body(imageData);
    }
}
