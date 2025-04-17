package kroryi.apikeymanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadViewController {

    @GetMapping("/upload-form")
    public String showUploadForm() {
        return "file_db_upload";  // upload.html
    }

    @GetMapping("/multi-upload-form")
    public String showMultiUploadForm() {
        return "file_db_multi_upload"; // multi-upload.html
    }


}