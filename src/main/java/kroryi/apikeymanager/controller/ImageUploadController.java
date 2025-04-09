package kroryi.apikeymanager.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Log4j2
@RestController
public class ImageUploadController {

    private final Path uploadPath = Paths.get("uploads");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(uploadPath);
    }

    @PostMapping("/uploadImage")
    public String handleImageUpload(@RequestParam("file") MultipartFile file) throws IOException {

        log.info("111111111111111 {}",file.getOriginalFilename());
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/" + filename;
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) throws IOException {
        Path file = uploadPath.resolve(filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
