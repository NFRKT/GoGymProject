package com.GoGym.controller;

import com.GoGym.module.*;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.repository.TrainerExperienceRepository;
import com.GoGym.repository.TrainerSpecializationRepository;
import com.GoGym.service.TrainerClientService;
import com.GoGym.service.TrainerDetailsService;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/trainer/profile")
public class TrainerProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private TrainerClientService trainerClientService;

    @Autowired
    private TrainerDetailsRepository trainerDetailsRepository;
    @Autowired
    private TrainerDetailsService trainerDetailsService;

    @Autowired
    private TrainerSpecializationRepository trainerSpecializationRepository;
    @Autowired
    private TrainerExperienceRepository trainerExperienceRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public String trainerProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(user.getIdUser()).orElse(null);
        List<TrainerSpecialization> specializations = trainerSpecializationRepository.findByTrainer(trainerDetails);
        List<TrainerExperience> experiences = trainerDetailsService.getTrainerExperience(user.getIdUser());

        model.addAttribute("user", user);
        model.addAttribute("trainerDetails", trainerDetails);
        model.addAttribute("specializations", specializations);
        model.addAttribute("experiences", experiences);
        return "trainer-profile";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (!file.isEmpty()) {
            try {
                User user = userService.findByEmail(principal.getName());
                TrainerDetails trainerDetails = trainerDetailsService.getTrainerDetails(user);

                if (trainerDetails == null) {
                    trainerDetails = new TrainerDetails();
                    trainerDetails.setUser(user);
                }

                // 🔥 Ścieżka do katalogu
                Path uploadDir = Paths.get("uploads/avatars/");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                // 🔥 Usunięcie starego zdjęcia (jeśli istnieje)
                if (trainerDetails.getProfilePicture() != null) {
                    Path oldFilePath = uploadDir.resolve(trainerDetails.getProfilePicture());
                    Files.deleteIfExists(oldFilePath);
                }

                // 🔥 Zapis nowego pliku
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath);

                // 🔥 Aktualizacja w bazie danych
                trainerDetails.setProfilePicture(fileName);
                trainerDetailsService.saveTrainerDetails(trainerDetails);

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
        TrainerDetails trainerDetails = trainerDetailsService.getTrainerDetails(user);

        if (trainerDetails != null && trainerDetails.getProfilePicture() != null) {
            try {
                // Ścieżka do katalogu
                Path filePath = Paths.get("uploads/avatars/").resolve(trainerDetails.getProfilePicture());

                // 🔥 Usunięcie pliku z serwera
                Files.deleteIfExists(filePath);

                // Usunięcie informacji o zdjęciu z bazy danych
                trainerDetails.setProfilePicture(null);
                trainerDetailsService.saveTrainerDetails(trainerDetails);

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
                    Path filePath = Paths.get("uploads/certifications/", fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.copy(certificationFile.getInputStream(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
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
            @RequestParam(value = "certificationFile", required = false) MultipartFile certificationFile) {

        Map<String, Object> response = new HashMap<>();
        Optional<TrainerExperience> experienceOpt = trainerDetailsService.findTrainerExperienceById(experienceId);

        if (experienceOpt.isPresent()) {
            TrainerExperience experience = experienceOpt.get();

            experience.setGraduationName(graduationName);
            experience.setGraduationDate(graduationDate);

            String fileName = experience.getCertificationFile(); // Zachowanie starego pliku, jeśli nowy nie został przesłany

            if (certificationFile != null && !certificationFile.isEmpty()) {
                try {
                    fileName = UUID.randomUUID() + "_" + certificationFile.getOriginalFilename();
                    Path filePath = Paths.get("uploads/certifications/", fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.copy(certificationFile.getInputStream(), filePath);
                    experience.setCertificationFile(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            trainerDetailsService.updateTrainerExperience(experience);

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

    @PostMapping("/deleteExperience")
    @ResponseBody
    public Map<String, Object> deleteExperience(@RequestParam("experienceId") Long experienceId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<TrainerExperience> experienceOpt = trainerDetailsService.findTrainerExperienceById(experienceId);

            if (experienceOpt.isPresent()) {
                TrainerExperience experience = experienceOpt.get();

                // 🔥 Jeśli istnieje plik certyfikatu, usuń go z serwera
                if (experience.getCertificationFile() != null) {
                    Path filePath = Paths.get("uploads/certifications/", experience.getCertificationFile());
                    Files.deleteIfExists(filePath);
                }

                // 🔥 Usuń doświadczenie z bazy danych
                trainerDetailsService.deleteTrainerExperience(experienceId);

                response.put("success", true);
            } else {
                response.put("success", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
        }

        return response;
    }


    @PostMapping("/updateStartDate")
    @ResponseBody
    public Map<String, Object> updateStartDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(user.getIdUser()).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setStartDate(startDate);
            trainerDetailsRepository.save(trainerDetails);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            response.put("success", true);
            response.put("startDate", dateFormat.format(startDate));
        } else {
            response.put("success", false);
        }
        return response;
    }

}
