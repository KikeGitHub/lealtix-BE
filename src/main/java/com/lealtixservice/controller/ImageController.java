package com.lealtixservice.controller;

import com.lealtixservice.dto.ImageDTO;
import com.lealtixservice.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Tag(name = "Image Controller", description = "Operaciones para la carga de imágenes")
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private  ImageService imageService;

    @Operation(summary = "Subir imagen en base64", description = "Sube una imagen en base64 a Cloudinary y retorna la URL.")
    @PostMapping(value = "/upload", consumes = "application/json")
    public ResponseEntity<String> uploadImage(@RequestBody ImageDTO imageDTO) {
        Long tenantId = null;
        try {
            if (imageDTO.getBase64File() == null || imageDTO.getBase64File().isEmpty()) {
                return ResponseEntity.badRequest().body("La imagen en base64 no puede estar vacía.");
            }
            if (imageDTO.getType() == null || imageDTO.getType().isEmpty()) {
                return ResponseEntity.badRequest().body("El tipo de imagen es obligatorio.");
            }
            tenantId = imageService.uploadImageBase64(imageDTO);
        } catch (Exception e) {
            log.error("Error al subir la imagen: ", e);
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
        return ResponseEntity.ok(tenantId.toString());
    }

    @Operation(summary = "Subir imagen para producto en base64", description = "Sube una imagen para producto en base64 a Cloudinary y retorna la URL.")
    @PostMapping(value = "/uploadImgProd", consumes = "application/json")
    public ResponseEntity<String> uploadImgProd(@RequestBody ImageDTO imageDTO) {
        String imageUrl = "";
        try {
            if (imageDTO.getBase64File() == null || imageDTO.getBase64File().isEmpty()) {
                return ResponseEntity.badRequest().body("La imagen en base64 no puede estar vacía.");
            }
            if (imageDTO.getType() == null || imageDTO.getType().isEmpty()) {
                return ResponseEntity.badRequest().body("El tipo de imagen es obligatorio.");
            }
            imageUrl = imageService.uploadProdImageBase64(imageDTO);
        } catch (Exception e) {
            log.error("Error al subir la imagen: ", e);
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
        return ResponseEntity.ok(imageUrl);
    }
}
