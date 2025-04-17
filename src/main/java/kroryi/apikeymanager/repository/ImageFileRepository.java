package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByNoticeId(Long noticeId);

}
