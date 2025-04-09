package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 필요 시 커스텀 쿼리 메소드 추가 가능
}
