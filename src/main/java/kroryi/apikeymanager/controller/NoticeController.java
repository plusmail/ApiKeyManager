package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.entity.ImageFile;
import kroryi.apikeymanager.entity.Notice;
import kroryi.apikeymanager.repository.NoticeRepository;
import kroryi.apikeymanager.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private final FileService fileService;

    // 📃 목록 조회
    @GetMapping
    public String list(Model model) {
        List<Notice> notices = noticeRepository.findAllByOrderByIdDesc();
        model.addAttribute("notices", notices);
        return "notice-list";
    }

    // 📝 등록 폼
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice-form";
    }

//    // ✅ 등록 처리
//    @PostMapping
//    public String submit(@ModelAttribute Notice notice) {
//        notice.setCreatedAt(LocalDateTime.now());
//        noticeRepository.save(notice);
//        return "redirect:/notices";
//    }

    @GetMapping("/{id}")
    public String viewNotice(@PathVariable Long id, Model model) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        // 각 첨부 파일에 publicUrl 필드 설정
        for (ImageFile file : notice.getImages()) {
            String publicUrl = fileService.getPublicUrl(file.getId());
            file.setPublicUrl(publicUrl); // <-- 아래에서 setter 구현해야 함
        }
        model.addAttribute("notice", notice);
        return "notice-detail"; // HTML 페이지에서 이미지도 함께 출력
    }


    @PostMapping
    public String saveNoticeWithImages(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);

        List<ImageFile> imageFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            ImageFile img = new ImageFile();
            img.setName(file.getOriginalFilename());
            img.setType(file.getContentType());
            img.setData(file.getBytes());
            img.setNotice(notice); // 관계 설정
            imageFiles.add(img);
        }

        notice.setImages(imageFiles);

        noticeRepository.save(notice); // 이미지까지 함께 저장됨 (Cascade)
        return "redirect:/notices"; // 리스트 페이지로 이동
    }
}
