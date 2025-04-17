package kroryi.apikeymanager.service;

import kroryi.apikeymanager.entity.ImageFile;
import kroryi.apikeymanager.repository.ImageFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageFileService {

    @Autowired
    private ImageFileRepository imageFileRepository;

    public ImageFile saveImage(MultipartFile file) throws IOException {
        ImageFile image = ImageFile.builder()
                .name(file.getOriginalFilename())
                .data(file.getBytes())
                .type(file.getContentType())
                .build();
        return imageFileRepository.save(image);
    }

    public Optional<ImageFile> getImage(Long id) {
        return imageFileRepository.findById(id);
    }
}
