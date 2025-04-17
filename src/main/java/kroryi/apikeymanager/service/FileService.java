package kroryi.apikeymanager.service;

import org.springframework.stereotype.Service;

@Service
public class FileService {
    public String getPublicUrl(Long id) {
        return "/files/file/" + id;
    }
}
