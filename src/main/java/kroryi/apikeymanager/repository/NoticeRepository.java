package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 기본적인 CRUD: findById, findAll, save, deleteById 등 모두 자동 제공
    // 제목으로 검색
    List<Notice> findByTitleContaining(String keyword);

    // 최신순 정렬
    List<Notice> findAllByOrderByIdDesc();
}
