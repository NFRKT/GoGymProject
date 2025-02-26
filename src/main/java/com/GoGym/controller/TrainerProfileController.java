package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.repository.TrainerSpecializationRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.TrainerDetailsService;
import com.GoGym.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/trainer/profile")
public class TrainerProfileController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerProfileController.class);
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";
    private static final String CERTIFICATION_UPLOAD_DIR = "uploads/certifications/";

    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainerDetailsRepository trainerDetailsRepository;
    private final TrainerDetailsService trainerDetailsService;
    private final TrainerSpecializationRepository trainerSpecializationRepository;

    @Autowired
    public TrainerProfileController(UserService userService,
                                    TrainerDetailsRepository trainerDetailsRepository,
                                    TrainerDetailsService trainerDetailsService,
                                    TrainerSpecializationRepository trainerSpecializationRepository, UserRepository userRepository) {
        this.userService = userService;
        this.trainerDetailsRepository = trainerDetailsRepository;
        this.trainerDetailsService = trainerDetailsService;
        this.trainerSpecializationRepository = trainerSpecializationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String trainerProfile(Model model, Principal principal,Authentication authentication) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(user.getIdUser()).orElse(null);
        List<TrainerSpecialization> specializations = trainerSpecializationRepository.findByTrainer(trainerDetails);
        List<TrainerExperience> experiences = trainerDetailsService.getTrainerExperience(user.getIdUser());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("trainerDetails", trainerDetails);
        model.addAttribute("specializations", specializations);
        model.addAttribute("experiences", experiences);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isAdmin", loggedInUser.getUserType() == User.UserType.ADMIN);
        return "trainer-profile";
    }

    @GetMapping("/{trainerId}")
    public String viewTrainerProfile(@PathVariable Long trainerId, Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.getPrincipal() instanceof CustomUserDetails;
        Long loggedInUserId = isAuthenticated ? ((CustomUserDetails) authentication.getPrincipal()).getUser().getIdUser() : null;

        User trainer = userService.findById(trainerId);
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(trainerId).orElse(null);
        List<TrainerSpecialization> specializations = trainerSpecializationRepository.findByTrainer(trainerDetails);
        List<TrainerExperience> experiences = trainerDetailsService.getTrainerExperience(trainerId);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User loggedInUser = userDetails.getUser();
        model.addAttribute("user", trainer);
        model.addAttribute("trainerDetails", trainerDetails);
        model.addAttribute("specializations", specializations);
        model.addAttribute("experiences", experiences);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("userId", loggedInUserId);
        model.addAttribute("isOwnProfile", isAuthenticated && loggedInUserId != null && loggedInUserId.equals(trainerId));
        model.addAttribute("isAdmin", loggedInUser.getUserType() == User.UserType.ADMIN);
        return "trainer-profile";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        // Walidacja, czy plik nie przekracza ustalonego rozmiaru (np. 5MB)
        long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > MAX_AVATAR_SIZE) {
            response.put("success", false);
            response.put("message", "Plik jest za duży. Maksymalny rozmiar to 5MB.");
            return response;
        }
        // Walidacja typu pliku – akceptujemy tylko obrazy
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            response.put("success", false);
            response.put("message", "Nieobsługiwany typ pliku. Akceptowane są tylko obrazy.");
            return response;
        }

        if (!file.isEmpty()) {
            try {
                User user = userService.findByEmail(principal.getName());
                TrainerDetails trainerDetails = trainerDetailsService.getTrainerDetails(user);
                if (trainerDetails == null) {
                    trainerDetails = new TrainerDetails();
                    trainerDetails.setUser(user);
                }

                Path uploadDir = Paths.get(AVATAR_UPLOAD_DIR);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // Usunięcie starego zdjęcia, jeśli istnieje
                if (trainerDetails.getProfilePicture() != null) {
                    Path oldFilePath = uploadDir.resolve(trainerDetails.getProfilePicture());
                    Files.deleteIfExists(oldFilePath);
                }

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                trainerDetails.setProfilePicture(fileName);
                trainerDetailsService.saveTrainerDetails(trainerDetails);

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
        TrainerDetails trainerDetails = trainerDetailsService.getTrainerDetails(user);

        if (trainerDetails != null && trainerDetails.getProfilePicture() != null) {
            try {
                Path filePath = Paths.get(AVATAR_UPLOAD_DIR).resolve(trainerDetails.getProfilePicture());
                Files.deleteIfExists(filePath);

                trainerDetails.setProfilePicture(null);
                trainerDetailsService.saveTrainerDetails(trainerDetails);

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
        TrainerDetails trainerDetails = trainerDetailsService.getTrainerDetails(user);
        if (trainerDetails == null) {
            trainerDetails = new TrainerDetails();
            trainerDetails.setUser(user);
        }

        switch (field) {
            case "bio":
                trainerDetails.setBio(value);
                break;
            case "phoneNumber":
                trainerDetails.setPhoneNumber(value);
                break;
            case "workArea":
                trainerDetails.setWorkArea(value);
                break;
            case "startDate":
                try {
                    trainerDetails.setStartDate(java.sql.Date.valueOf(value));
                } catch (IllegalArgumentException e) {
                    response.put("success", false);
                    return response;
                }
                break;
            default:
                response.put("success", false);
                return response;
        }

        trainerDetailsService.saveTrainerDetails(trainerDetails);
        response.put("success", true);
        response.put(field, value);
        return response;
    }

    @PostMapping("/deleteSpecialization")
    @ResponseBody
    public Map<String, Object> deleteSpecialization(@RequestParam("specializationId") Long specializationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            trainerSpecializationRepository.deleteById(specializationId);
            response.put("success", true);
        } catch (Exception e) {
            logger.error("Błąd podczas usuwania specjalizacji", e);
            response.put("success", false);
        }
        return response;
    }

    @PostMapping("/updateSpecialization")
    @ResponseBody
    public Map<String, Object> updateSpecialization(@RequestParam("specializationId") Long specializationId,
                                                    @RequestParam("specialization") String specialization) {
        Map<String, Object> response = new HashMap<>();
        Optional<TrainerSpecialization> specializationOpt = trainerSpecializationRepository.findById(specializationId);
        if (specializationOpt.isPresent()) {
            TrainerSpecialization existingSpecialization = specializationOpt.get();
            existingSpecialization.setSpecialization(specialization);
            trainerSpecializationRepository.save(existingSpecialization);

            response.put("success", true);
            response.put("specializationId", existingSpecialization.getId());
            response.put("specialization", existingSpecialization.getSpecialization());
        } else {
            response.put("success", false);
        }
        return response;
    }

    @PostMapping("/addSpecialization")
    @ResponseBody
    public Map<String, Object> addSpecialization(@RequestParam("specialization") String specialization, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(user.getIdUser()).orElse(null);

        if (trainerDetails != null && specialization != null && !specialization.trim().isEmpty()) {
            TrainerSpecialization newSpecialization = new TrainerSpecialization(specialization, trainerDetails);
            trainerSpecializationRepository.save(newSpecialization);
            response.put("success", true);
            response.put("specializationId", newSpecialization.getId());
            response.put("specialization", newSpecialization.getSpecialization());
        } else {
            response.put("success", false);
        }
        return response;
    }

    @PostMapping("/addExperience")
    @ResponseBody
    public Map<String, Object> addExperience(
            @RequestParam("graduationName") String graduationName,
            @RequestParam(value = "graduationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date graduationDate,
            @RequestParam(value = "certificationFile", required = false) MultipartFile certificationFile,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(user.getIdUser()).orElse(null);

        if (trainerDetails != null && !graduationName.trim().isEmpty()) {
            String fileName = null;
            if (certificationFile != null && !certificationFile.isEmpty()) {
                try {
                    fileName = UUID.randomUUID() + "_" + certificationFile.getOriginalFilename();
                    Path filePath = Paths.get(CERTIFICATION_UPLOAD_DIR, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.copy(certificationFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("Błąd podczas uploadu certyfikatu", e);
                }
            }

            TrainerExperience experience = new TrainerExperience(trainerDetails, graduationName, graduationDate, fileName);
            trainerDetailsService.addTrainerExperience(experience);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = (graduationDate != null) ? dateFormat.format(graduationDate) : "Nie podano";

            response.put("success", true);
            response.put("experienceId", experience.getId());
            response.put("graduationName", experience.getGraduationName());
            response.put("graduationDate", formattedDate);
            response.put("certificationFile", fileName);
        } else {
            response.put("success", false);
        }
        return response;
    }

    @PostMapping("/updateExperience")
    @ResponseBody
    public Map<String, Object> updateExperience(
            @RequestParam("experienceId") Long experienceId,
            @RequestParam("graduationName") String graduationName,
            @RequestParam(value = "graduationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date graduationDate,
            @RequestParam(value = "certificationFile", required = false) MultipartFile certificationFile,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        Optional<TrainerExperience> experienceOpt = trainerDetailsService.findTrainerExperienceById(experienceId);

        if (!experienceOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "Nie znaleziono doświadczenia");
            return response;
        }

        TrainerExperience experience = experienceOpt.get();
        // Sprawdzenie, czy doświadczenie należy do zalogowanego trenera
        if (!experience.getTrainer().getUser().getIdUser().equals(user.getIdUser())) {
            response.put("success", false);
            response.put("message", "Brak uprawnień do edycji tego doświadczenia");
            return response;
        }

        experience.setGraduationName(graduationName);
        experience.setGraduationDate(graduationDate);

        if (certificationFile != null && !certificationFile.isEmpty()) {
            // Walidacja typu pliku – akceptujemy PDF oraz obrazy
            String contentType = certificationFile.getContentType();
            if (!(("application/pdf".equals(contentType)) || (contentType != null && contentType.startsWith("image/")))) {
                response.put("success", false);
                response.put("message", "Nieobsługiwany typ pliku certyfikatu");
                return response;
            }
            // Walidacja rozmiaru pliku (np. maks. 10MB)
            long MAX_CERTIFICATION_SIZE = 10 * 1024 * 1024; // 10MB
            if (certificationFile.getSize() > MAX_CERTIFICATION_SIZE) {
                response.put("success", false);
                response.put("message", "Plik jest za duży. Maksymalny rozmiar to 10MB.");
                return response;
            }
            try {
                // Usunięcie starego pliku, jeśli istnieje
                if (experience.getCertificationFile() != null) {
                    Path oldFilePath = Paths.get(CERTIFICATION_UPLOAD_DIR, experience.getCertificationFile());
                    Files.deleteIfExists(oldFilePath);
                }
                // Zapis nowego pliku
                String fileName = UUID.randomUUID() + "_" + certificationFile.getOriginalFilename();
                Path filePath = Paths.get(CERTIFICATION_UPLOAD_DIR, fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(certificationFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                experience.setCertificationFile(fileName);
            } catch (IOException e) {
                logger.error("Błąd podczas aktualizacji certyfikatu", e);
                response.put("success", false);
                response.put("message", "Błąd podczas zapisu pliku");
                return response;
            }
        }

        trainerDetailsService.updateTrainerExperience(experience);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = (graduationDate != null) ? dateFormat.format(graduationDate) : "Nie podano";

        response.put("success", true);
        response.put("experienceId", experience.getId());
        response.put("graduationName", experience.getGraduationName());
        response.put("graduationDate", formattedDate);
        response.put("certificationFile", experience.getCertificationFile());
        return response;
    }

    @PostMapping("/deleteExperience")
    @ResponseBody
    public Map<String, Object> deleteExperience(@RequestParam("experienceId") Long experienceId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        Optional<TrainerExperience> experienceOpt = trainerDetailsService.findTrainerExperienceById(experienceId);

        if (!experienceOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "Nie znaleziono doświadczenia");
            return response;
        }

        TrainerExperience experience = experienceOpt.get();
        // Sprawdzenie, czy doświadczenie należy do zalogowanego trenera
        if (!experience.getTrainer().getUser().getIdUser().equals(user.getIdUser())) {
            response.put("success", false);
            response.put("message", "Brak uprawnień do usunięcia tego doświadczenia");
            return response;
        }

        try {
            if (experience.getCertificationFile() != null) {
                Path filePath = Paths.get(CERTIFICATION_UPLOAD_DIR, experience.getCertificationFile());
                Files.deleteIfExists(filePath);
            }
            trainerDetailsService.deleteTrainerExperience(experienceId);
            response.put("success", true);
        } catch (IOException e) {
            logger.error("Błąd podczas usuwania doświadczenia", e);
            response.put("success", false);
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
        if (firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Imię i nazwisko są wymagane.");
            return response;
        }
        user.setFirstName(firstName.trim());
        user.setSecondName(lastName.trim());
        userRepository.save(user);
        response.put("success", true);
        return response;
    }

    @PostMapping("/updateBirthDate")
    @ResponseBody
    public Map<String, Object> updateBirthDate(@RequestParam("birthDate")
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,
                                               Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        if (birthDate == null) {
            response.put("success", false);
            response.put("message", "Data urodzenia jest wymagana.");
            return response;
        }
        Date today = new Date();
        // Ustawiamy godziny na 0, aby porównanie było tylko datowe
        Calendar calToday = Calendar.getInstance();
        calToday.setTime(today);
        calToday.set(Calendar.HOUR_OF_DAY, 0);
        calToday.set(Calendar.MINUTE, 0);
        calToday.set(Calendar.SECOND, 0);
        calToday.set(Calendar.MILLISECOND, 0);
        if (birthDate.after(calToday.getTime())) {
            response.put("success", false);
            response.put("message", "Data urodzenia nie może być w przyszłości.");
            return response;
        }
        user.setBirthDate(birthDate);
        userRepository.save(user);
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
            // Zakładamy, że enum w klasie User nazywa się Gender i ma wartości KOBIETA oraz MĘŻCZYZNA.
            // Używamy toUpperCase() aby mieć pewność, że przekazany tekst odpowiada nazwie enuma.
            User.Gender genderEnum = User.Gender.valueOf(gender.trim().toUpperCase());
            user.setGender(genderEnum);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Podano nieprawidłową wartość płci. Wybierz KOBIETA lub MĘŻCZYZNA.");
            return response;
        }
        userRepository.save(user);
        response.put("success", true);
        return response;
    }


}
