package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByPostDate(LocalDate date);
}
