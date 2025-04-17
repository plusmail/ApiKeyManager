package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
