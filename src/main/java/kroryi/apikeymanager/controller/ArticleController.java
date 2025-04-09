package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.dto.FormDTO;
import kroryi.apikeymanager.entity.Article;
import kroryi.apikeymanager.repository.ArticleRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/editor")
    public String uploadImage(Model model) {
        model.addAttribute("form", new FormDTO());  // 폼 객체 전달

        return "editor";
    }
    @PostMapping("/submit")
    public String handleFormSubmit(@ModelAttribute("form") FormDTO formDto) {
        String htmlContent = formDto.getContent(); // Summernote HTML 본문
        System.out.println("본문 HTML: " + htmlContent);

        Article article = Article.builder()
                .content(htmlContent)
                .build();
        articleRepository.save(article);

        return "redirect:/editor"; // 완료 후 페이지 이동
    }
    @GetMapping("/articles/{id}")
    public String viewArticle(@PathVariable Long id, Model model) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));

        model.addAttribute("article", article);
        return "article-view";
    }

    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
    public void cleanupUnusedImages() {
        Path baseDir = Paths.get("uploads");

        try (Stream<Path> allFiles = Files.walk(baseDir)) {
            List<Path> imageFiles = allFiles
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            // DB에 있는 모든 게시글의 content를 가져와서 포함된 이미지 경로 추출
            List<String> usedImages = articleRepository.findAll().stream()
                    .flatMap(article -> extractImagePathsFromHtml(article.getContent()).stream())
                    .collect(Collectors.toList());

            for (Path file : imageFiles) {
                String fileWebPath = "/images/" + baseDir.relativize(file).toString().replace("\\", "/");

                if (!usedImages.contains(fileWebPath)) {
                    Files.delete(file); // 사용되지 않는 파일 삭제
                    System.out.println("삭제된 이미지: " + fileWebPath);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> extractImagePathsFromHtml(String html) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            result.add(matcher.group(1)); // src 속성 값
        }
        return result;
    }
}
