package kroryi.apikeymanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class SmartEditorController {

    @Value("${file.location}")
    private String fileLocation;

    @PostMapping("/editor_upload")
    @ResponseBody
    public String upload(HttpServletRequest request) throws IOException {
        String sFileInfo = "";

        String filename = request.getHeader("file-name");
        String ext = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");

        List<String> allowedExt = List.of("jpg", "png", "bmp", "gif");

        if (!allowedExt.contains(ext)) {
            return "NOTALLOW_" + filename;
        }

        // 디렉토리 생성
        Path uploadDir = Paths.get(fileLocation);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 파일 이름 생성
        String today = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String newFileName = today + UUID.randomUUID() + "." + ext;
        Path outputPath = uploadDir.resolve(newFileName);

        // 파일 저장
        try (InputStream is = request.getInputStream();
             OutputStream os = Files.newOutputStream(outputPath)) {

            byte[] buffer = new byte[Integer.parseInt(request.getHeader("file-size"))];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }

        sFileInfo += "&bNewLine=true";
        sFileInfo += "&sFileName=" + filename;
        sFileInfo += "&sFileURL=/editor/images/" + newFileName;

        System.out.println(sFileInfo);
        return sFileInfo;
    }

    @GetMapping("/editor/images/{imageName:.+}")
    public ResponseEntity<byte[]> editorImage(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(fileLocation, imageName);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType = Files.probeContentType(imagePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        byte[] imageBytes;
        try (InputStream is = Files.newInputStream(imagePath)) {
            imageBytes = IOUtils.toByteArray(is);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }
}
