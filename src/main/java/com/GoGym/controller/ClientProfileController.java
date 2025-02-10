package com.GoGym.controller;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.TrainerDetails;
import com.GoGym.module.User;
import com.GoGym.service.ClientDetailsService;
import com.GoGym.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/client/profile")
public class ClientProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientDetailsService clientDetailsService;

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
            try {
                User user = userService.findByEmail(principal.getName());
                ClientDetails clientDetails = clientDetailsService.getClientDetails(user);

                if (clientDetails == null) {
                    clientDetails = new ClientDetails();
                    clientDetails.setUser(user);
                }

                // 🔥 Ścieżka do katalogu
                Path uploadDir = Paths.get("uploads/avatars/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // 🔥 Usunięcie starego zdjęcia (jeśli istnieje)
                if (clientDetails.getProfilePicture() != null) {
                    Path oldFilePath = uploadDir.resolve(clientDetails.getProfilePicture());
                    Files.deleteIfExists(oldFilePath);
                }

                // 🔥 Zapis nowego pliku
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                // 🔥 Aktualizacja w bazie danych
                clientDetails.setProfilePicture(fileName);
                clientDetailsService.saveClientDetails(clientDetails);

                // 🔥 Odpowiedź JSON
                response.put("success", true);
                response.put("profilePictureUrl", "/uploads/avatars/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
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
                // Ścieżka do katalogu
                Path filePath = Paths.get("uploads/avatars/").resolve(clientDetails.getProfilePicture());

                // 🔥 Usunięcie pliku z serwera
                Files.deleteIfExists(filePath);

                // Usunięcie informacji o zdjęciu z bazy danych
                clientDetails.setProfilePicture(null);
                clientDetailsService.saveClientDetails(clientDetails);

                response.put("success", true);
                response.put("defaultProfilePicture", "/images/default-profile.png");

            } catch (IOException e) {
                e.printStackTrace();
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
