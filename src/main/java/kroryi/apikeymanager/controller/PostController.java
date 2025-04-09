package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.entity.Post;
import kroryi.apikeymanager.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    @GetMapping("/{date}")
    public List<Post> getPosts(@PathVariable String date) {
        return postRepository.findByPostDate(LocalDate.parse(date));
    }
}
