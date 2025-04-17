package kroryi.apikeymanager.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import kroryi.apikeymanager.entity.ImageFile;
import kroryi.apikeymanager.service.ImageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/images")
public class ImageFileDBController {

    @Autowired
    private ImageFileService imageFileService;

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

}
