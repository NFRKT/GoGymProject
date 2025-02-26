package com.GoGym.controller;

import com.GoGym.module.ClientDetails;
import com.GoGym.module.User;
import com.GoGym.module.Badge;
import com.GoGym.module.UserBadge;
import com.GoGym.service.BadgeService;
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
import java.util.*;

@Controller
@RequestMapping("/client/profile")
public class ClientProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ClientProfileController.class);
    private static final Path AVATAR_UPLOAD_DIR = Paths.get("uploads/avatars/");
    private static final long MAX_AVATAR_SIZE = 10 * 1024 * 1024;

    private final UserService userService;
    private final ClientDetailsService clientDetailsService;
    private final BadgeService badgeService;

    public ClientProfileController(UserService userService, ClientDetailsService clientDetailsService, BadgeService badgeService) {
        this.userService = userService;
        this.clientDetailsService = clientDetailsService;
        this.badgeService = badgeService;
    }

    @GetMapping
    public String clientProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        // Upewnij się, że użytkownik jest klientem
        if (user.getUserType() != User.UserType.CLIENT) {
            throw new IllegalStateException("Tylko klienci mają profil klienta.");
        }
        ClientDetails clientDetails = clientDetailsService.getClientDetails(user);
        if (clientDetails == null) {
            clientDetails = new ClientDetails();
            clientDetails.setUser(user);
            clientDetails.setProfilePicture(null);
        }
        List<UserBadge> badges = badgeService.getBadgesForUser(user);
        Map<Long, String> badgeProgress = badgeService.calculateBadgeProgress(user);

        model.addAttribute("user", user);
        model.addAttribute("clientDetails", clientDetails);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("badges", badges);
        model.addAttribute("badgeProgress", badgeProgress);
        return "client-profile";
    }

    @GetMapping("/{clientId}")
    public String viewProfile(@PathVariable Long clientId, Model model) {
        User user = userService.findById(clientId);
        if (user == null || user.getUserType() != User.UserType.CLIENT) {
            throw new IllegalArgumentException("Nie znaleziono klienta o ID: " + clientId);
        }
        ClientDetails clientDetails = clientDetailsService.getClientDetails(user);
        if (clientDetails == null) {
            clientDetails = new ClientDetails();
            clientDetails.setUser(user);
        }
        // Dla profilu innego użytkownika isOwnProfile = false, odznaki mogą być wyświetlone, jeśli chcesz
        List<UserBadge> badges = badgeService.getBadgesForUser(user);
        Map<Long, String> badgeProgress = badgeService.calculateBadgeProgress(user);
        model.addAttribute("user", user);
        model.addAttribute("clientDetails", clientDetails);
        model.addAttribute("isOwnProfile", false);
        model.addAttribute("badges", badges);
        model.addAttribute("badgeProgress", badgeProgress);
        return "client-profile";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "Nieobsługiwany typ pliku. Akceptowane są tylko obrazy.");
                return response;
            }
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
                if (clientDetails.getProfilePicture() != null) {
                    Path oldFilePath = AVATAR_UPLOAD_DIR.resolve(clientDetails.getProfilePicture());
                    Files.deleteIfExists(oldFilePath);
                }
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = AVATAR_UPLOAD_DIR.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
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

    @PostMapping("/updateName")
    @ResponseBody
    public Map<String, Object> updateName(@RequestParam("firstName") String firstName,
                                          @RequestParam("lastName") String lastName,
                                          Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        if (firstName == null || firstName.trim().length() < 2 || !firstName.trim().matches("^[A-ZĄĆĘŁŃÓŚŹŻ].*")) {
            response.put("success", false);
            response.put("message", "Imię musi mieć co najmniej 2 znaki i zaczynać się od dużej litery.");
            return response;
        }
        if (lastName == null || lastName.trim().length() < 2 || !lastName.trim().matches("^[A-ZĄĆĘŁŃÓŚŹŻ].*")) {
            response.put("success", false);
            response.put("message", "Nazwisko musi mieć co najmniej 2 znaki i zaczynać się od dużej litery.");
            return response;
        }
        user.setFirstName(firstName.trim());
        user.setSecondName(lastName.trim());
        userService.saveUser(user);
        response.put("success", true);
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getSecondName());
        return response;
    }

    @PostMapping("/updateBirthDate")
    @ResponseBody
    public Map<String, Object> updateBirthDate(@RequestParam("birthDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,
                                               Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        if (birthDate == null) {
            response.put("success", false);
            response.put("message", "Data urodzenia jest wymagana.");
            return response;
        }
        Date today = new Date();
        if (birthDate.after(today)) {
            response.put("success", false);
            response.put("message", "Data urodzenia nie może być w przyszłości.");
            return response;
        }
        user.setBirthDate(birthDate);
        userService.saveUser(user);
        response.put("success", true);
        return response;
    }

    @PostMapping("/updateGender")
    @ResponseBody
    public Map<String, Object> updateGender(@RequestParam("gender") String gender,
                                            Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        if (gender == null || gender.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Płeć jest wymagana.");
            return response;
        }
        try {
            User.Gender genderEnum = User.Gender.valueOf(gender.trim().toUpperCase());
            user.setGender(genderEnum);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Podano nieprawidłową wartość płci. Wybierz KOBIETA lub MĘŻCZYZNA.");
            return response;
        }
        userService.saveUser(user);
        response.put("success", true);
        return response;
    }
}
