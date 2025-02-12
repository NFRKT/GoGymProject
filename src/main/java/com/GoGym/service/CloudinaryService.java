package com.GoGym.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        long maxSize = 100 * 1024 * 1024; // 100MB limit

        if (file.getSize() > maxSize) {
            throw new MaxUploadSizeExceededException(maxSize);
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video",
                        "quality", "auto:eco",
                        "bit_rate", "1000k"
                ));

        return uploadResult.get("secure_url").toString();
    }

    // 🔥 Nowa metoda usuwania pliku z Cloudinary
    public void deleteVideo(String videoUrl) throws Exception {
        if (!videoUrl.contains("cloudinary.com")) {
            return; // Jeśli to nie Cloudinary, nie usuwamy
        }

        String publicId = extractPublicId(videoUrl);
        System.out.println("Usuwanie pliku Cloudinary o ID: " + publicId);

        Map result = cloudinary.api().deleteResources(
                Collections.singletonList(publicId), // ✅ Przekazanie poprawnego typu List<String>
                ObjectUtils.asMap("resource_type", "video")
        );

        System.out.println("Wynik usunięcia: " + result);
    }

    // 🔍 Metoda wyciągająca poprawny `public_id` z URL Cloudinary
    private String extractPublicId(String videoUrl) {
        String[] parts = videoUrl.split("/");
        String filename = parts[parts.length - 1]; // Ostatni segment URL
        return filename.split("\\.")[0]; // Usunięcie rozszerzenia .mp4
    }
}
