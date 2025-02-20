package com.GoGym.controller;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.User;
import com.GoGym.service.ClientDetailsService;
import com.GoGym.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/client/profile")
public class ClientProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ClientProfileController.class);
    private static final Path AVATAR_UPLOAD_DIR = Paths.get("uploads/avatars/");
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

    private final UserService userService;
    private final ClientDetailsService clientDetailsService;

    public ClientProfileController(UserService userService, ClientDetailsService clientDetailsService) {
        this.userService = userService;
        this.clientDetailsService = clientDetailsService;
    }

    @GetMapping
    public String clientProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ClientDetails clientDetails = clientDetailsService.getClientDetails(user);

        if (clientDetails == null) {
            clientDetails = new ClientDetails();
            clientDetails.setUser(user);
            clientDetails.setProfilePicture(null); // Wyraźnie ustaw NULL dla nowego użytkownika
        }

        model.addAttribute("user", user);
        model.addAttribute("clientDetails", clientDetails);

        return "client-profile";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (!file.isEmpty()) {
            // Walidacja typu pliku (akceptujemy tylko obrazy)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Nieobsługiwany typ pliku. Akceptowane są tylko obrazy.");
                return response;
            }
            // Walidacja rozmiaru pliku
            if (file.getSize() > MAX_AVATAR_SIZE) {
                response.put("success", false);
                response.put("message", "Plik jest za duży. Maksymalny rozmiar to 5MB.");
                return response;
            }

            try {
                User user = userService.findByEmail(principal.getName());
                ClientDetails clientDetails = clientDetailsService.getClientDetails(user);

                if (clientDetails == null) {
                    clientDetails = new ClientDetails();
                    clientDetails.setUser(user);
                }

                if (!Files.exists(AVATAR_UPLOAD_DIR)) {
                    Files.createDirectories(AVATAR_UPLOAD_DIR);
                }

                // Usunięcie starego zdjęcia, jeśli istnieje
                if (clientDetails.getProfilePicture() != null) {
                    Path oldFilePath = AVATAR_UPLOAD_DIR.resolve(clientDetails.getProfilePicture());
                    Files.deleteIfExists(oldFilePath);
                }

                // Zapis nowego pliku
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = AVATAR_UPLOAD_DIR.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Aktualizacja w bazie danych
                clientDetails.setProfilePicture(fileName);
                clientDetailsService.saveClientDetails(clientDetails);

                response.put("success", true);
                response.put("profilePictureUrl", "/uploads/avatars/" + fileName);

            } catch (IOException e) {
                logger.error("Błąd podczas uploadu zdjęcia profilowego", e);
                response.put("success", false);
            }
        } else {
            response.put("success", false);
        }

        return response;
    }

    @PostMapping("/deleteProfilePicture")
    @ResponseBody
    public Map<String, Object> deleteProfilePicture(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        ClientDetails clientDetails = clientDetailsService.getClientDetails(user);

        if (clientDetails != null && clientDetails.getProfilePicture() != null) {
            try {
                Path filePath = AVATAR_UPLOAD_DIR.resolve(clientDetails.getProfilePicture());
                Files.deleteIfExists(filePath);

                clientDetails.setProfilePicture(null);
                clientDetailsService.saveClientDetails(clientDetails);

                response.put("success", true);
                response.put("defaultProfilePicture", "/images/default-profile.png");

            } catch (IOException e) {
                logger.error("Błąd podczas usuwania zdjęcia profilowego", e);
                response.put("success", false);
            }
        } else {
            response.put("success", false);
        }

        return response;
    }

    @PostMapping("/updateField")
    @ResponseBody
    public Map<String, Object> updateField(@RequestParam String field, @RequestParam String value, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        ClientDetails clientDetails = clientDetailsService.getClientDetails(user);

        if (clientDetails == null) {
            clientDetails = new ClientDetails();
            clientDetails.setUser(user);
        }

        try {
            switch (field) {
                case "weight":
                    clientDetails.setWeight(Double.parseDouble(value));
                    break;
                case "height":
                    clientDetails.setHeight(Double.parseDouble(value));
                    break;
                case "waist":
                    clientDetails.setWaist(Double.parseDouble(value));
                    break;
                case "hips":
                    clientDetails.setHips(Double.parseDouble(value));
                    break;
                case "chest":
                    clientDetails.setChest(Double.parseDouble(value));
                    break;
                case "phoneNumber":
                    clientDetails.setPhoneNumber(value);
                    break;
                case "city":
                    clientDetails.setCity(value);
                    break;
                default:
                    response.put("success", false);
                    response.put("error", "Nieznane pole.");
                    return response;
            }

            clientDetailsService.saveClientDetails(clientDetails);
            response.put("success", true);
            response.put(field, value);
        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("error", "Błędny format liczby dla pola: " + field);
        }

        return response;
    }
}
