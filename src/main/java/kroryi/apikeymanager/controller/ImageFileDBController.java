package kroryi.apikeymanager.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import kroryi.apikeymanager.entity.ImageFile;
import kroryi.apikeymanager.repository.ImageFileRepository;
import kroryi.apikeymanager.service.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
public class ImageFileDBController {

    @Autowired
    private ImageFileService imageFileService;

    @Autowired
    private ImageFileRepository imageFileRepository;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 여러 개 업로드", description = "여러 이미지를 동시에 업로드합니다.")
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(type = "string", format = "binary")
            ))
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ImageFile saved = imageFileService.saveImage(file);
            return ResponseEntity.ok("Uploaded with ID: " + saved.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    @PostMapping(value = "/multi-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 여러 개 업로드", description = "여러 이미지를 동시에 업로드합니다.")
    public ResponseEntity<String> uploadMultipleImages(
            @Parameter(
                    description = "여러 개의 이미지 파일",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {
        StringBuilder result = new StringBuilder();
        for (MultipartFile file : files) {
            // 저장 로직 (서비스에서 처리 가능)
            ImageFile saved = imageFileService.saveImage(file);
            result.append(file.getOriginalFilename()).append(" 업로드 완료\n");

        }
        return ResponseEntity.ok(result.toString());
    }


    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<ImageFile> image = imageFileService.getImage(id);
        if (image.isPresent()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.get().getType()))
                    .body(image.get().getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> serveFile(@PathVariable Long id) {
        ImageFile img = imageFileRepository.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img.getType()))
                .body(img.getData());
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        ImageFile file = imageFileRepository.findById(id).orElseThrow();
        String filename = file.getName();

        // RFC 5987 방식으로 UTF-8 파일명 인코딩
        String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);

        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFilename;

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(file.getData());
    }


}
