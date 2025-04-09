package kroryi.apikeymanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CKBoxExampleWebpageController {
    @GetMapping("/ckbox-example-ckeditor")
    public String ckeditorExample(Model model) {
        return "ckbox-example-ckeditor";
    }

    @GetMapping("/ckbox-example-modal")
    public String modalExample(Model model) {
        return "ckbox-example-modal";
    }

    @GetMapping("/ckbox-example-full-page")
    public String fullPageExample(Model model) {
        return "ckbox-example-full-page";
    }
}