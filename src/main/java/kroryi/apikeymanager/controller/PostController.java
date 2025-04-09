package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.dto.FormDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {



    @GetMapping("/editor")
    public String uploadImage(Model model) {
        model.addAttribute("form", new FormDTO());  // 폼 객체 전달

        return "editor";
    }
    @PostMapping("/submit")
    public String handleFormSubmit(@ModelAttribute("form") FormDTO formDto) {
        String htmlContent = formDto.getContent(); // Summernote HTML 본문
        System.out.println("본문 HTML: " + htmlContent);

        // 예시: DB에 저장하거나 파일로 저장할 수 있음
        // saveToDatabase(htmlContent);
        // 또는 writeToFile(htmlContent);

        return "redirect:/editor"; // 완료 후 페이지 이동
    }

    @GetMapping("/write")
    public String writeForm() {
        return "tinymce_writer"; // templates/write.html
    }

    @PostMapping("/write")
    public String submitPost(@RequestParam String title,
                             @RequestParam String content,
                             Model model) {

        model.addAttribute("title", title);
        model.addAttribute("content", content);
        return "result"; // templates/result.html
    }
}
