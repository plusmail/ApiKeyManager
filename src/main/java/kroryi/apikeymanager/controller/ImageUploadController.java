package kroryi.apikeymanager.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@RestController
public class ImageUploadController {

    private final Path uploadPath = Paths.get("uploads");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        // 날짜별 폴더 생성
        String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get("uploads").resolve(dateFolder);

        try {
            Files.createDirectories(uploadPath);

            String originalFilename = file.getOriginalFilename();
            String ext = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            }

            String storedFileName = UUID.randomUUID() + "." + ext;
            Path targetPath = uploadPath.resolve(storedFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 이미지 URL 반환
            String fileUrl = "/images/" + dateFolder + "/" + storedFileName;
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 실패");
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws IOException {
        Path filePath = uploadPath.resolve(filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() || resource.isReadable()) {
            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
