package com.lealtixservice.service;

import com.lealtixservice.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile file, String type, ImageDTO tenant) throws IOException;

    Long uploadImageBase64(ImageDTO imageDTO) throws IOException;
}
