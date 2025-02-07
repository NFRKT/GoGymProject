package com.GoGym.controller;

import com.GoGym.module.TrainerDetails;
import com.GoGym.module.TrainerExperience;
import com.GoGym.module.TrainerSpecialization;
import com.GoGym.module.User;
import com.GoGym.repository.TrainerDetailsRepository;
import com.GoGym.repository.TrainerExperienceRepository;
import com.GoGym.repository.TrainerSpecializationRepository;
import com.GoGym.service.TrainerClientService;
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
    private TrainerSpecializationRepository trainerSpecializationRepository;
    @Autowired
    private TrainerExperienceRepository trainerExperienceRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public String trainerProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);
        List<TrainerSpecialization> specializations = trainerSpecializationRepository.findByTrainer(trainerDetails);
        List<TrainerExperience> experiences = trainerClientService.getTrainerExperience(user.getIdUser());

        model.addAttribute("user", user);
        model.addAttribute("trainerDetails", trainerDetails);
        model.addAttribute("specializations", specializations);
        model.addAttribute("experiences", experiences);
        return "trainer-profile";
    }



    @PostMapping("/upload")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                       Principal principal) {
        if (!file.isEmpty()) {
            try {
                User user = userService.findByEmail(principal.getName());
                TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

                if (trainerDetails != null) {
                    // Sprawdzenie i utworzenie katalogu jeśli nie istnieje
                    Path uploadDir = Paths.get("uploads/avatars/");
                    if (!Files.exists(uploadDir)) {
                        Files.createDirectories(uploadDir);
                    }

                    // Generowanie unikalnej nazwy pliku
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(fileName);

                    // Zapisywanie pliku
                    Files.copy(file.getInputStream(), filePath);

                    // Aktualizacja w bazie danych
                    trainerDetails.setProfilePicture(fileName);
                    trainerDetailsRepository.save(trainerDetails);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/trainer/profile";
    }
    @PostMapping("/deleteProfilePicture")
    public String deleteProfilePicture(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setProfilePicture(null);
            trainerDetailsRepository.save(trainerDetails);
        }

        return "redirect:/trainer/profile";
    }

    @PostMapping("/updateBio")
    @ResponseBody
    public Map<String, Object> updateBio(@RequestBody Map<String, String> requestBody, Principal principal) {
        Map<String, Object> response = new HashMap<>();

        String bio = requestBody.get("bio"); // Pobranie wartości z JSON-a

        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setBio(bio);
            trainerDetailsRepository.save(trainerDetails);

            response.put("success", true);
            response.put("bio", trainerDetails.getBio());
        } else {
            response.put("success", false);
        }

        return response; // Zwracamy JSON-a
    }


    @PostMapping("/deleteSpecialization")
    public String deleteSpecialization(@RequestParam("specializationId") Long specializationId) {
        trainerSpecializationRepository.deleteById(specializationId);
        return "redirect:/trainer/profile";
    }

    @PostMapping("/addSpecialization")
    public String addSpecialization(@RequestParam("specialization") String specialization, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null && specialization != null && !specialization.trim().isEmpty()) {
            TrainerSpecialization newSpecialization = new TrainerSpecialization(specialization, trainerDetails);
            trainerSpecializationRepository.save(newSpecialization);
        }

        return "redirect:/trainer/profile";
    }
    @PostMapping("/addExperience")
    public String addExperience(@RequestParam("graduationName") String graduationName,
                                @RequestParam(value = "graduationDate", required = false)
                                @DateTimeFormat(pattern = "yyyy-MM-dd") Date graduationDate,
                                @RequestParam(value = "certificationFile", required = false) MultipartFile certificationFile,
                                Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            String fileName = null;
            if (!certificationFile.isEmpty()) {
                try {
                    fileName = UUID.randomUUID() + "_" + certificationFile.getOriginalFilename();
                    Path filePath = Paths.get("uploads/certifications/", fileName);
                    Files.createDirectories(filePath.getParent()); // Tworzenie folderu, jeśli nie istnieje
                    Files.copy(certificationFile.getInputStream(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            TrainerExperience experience = new TrainerExperience(trainerDetails, graduationName, graduationDate, fileName);
            trainerClientService.addTrainerExperience(experience);
        }

        return "redirect:/trainer/profile";
    }

    @PostMapping("/deleteExperience")
    public String deleteExperience(@RequestParam("experienceId") Long experienceId) {
        trainerClientService.deleteTrainerExperience(experienceId);
        return "redirect:/trainer/profile";
    }
    @PostMapping("/updateStartDate")
    public String updateStartDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setStartDate(startDate);
            trainerDetailsRepository.save(trainerDetails);
        }

        return "redirect:/trainer/profile";
    }

    @PostMapping("/updatePhoneNumber")
    public String updatePhoneNumber(@RequestParam("phoneNumber") String phoneNumber, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setPhoneNumber(phoneNumber);
            trainerDetailsRepository.save(trainerDetails);
        }

        return "redirect:/trainer/profile";
    }

    @PostMapping("/updateWorkArea")
    public String updateWorkArea(@RequestParam("workArea") String workArea, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        TrainerDetails trainerDetails = trainerDetailsRepository.findById(Math.toIntExact(user.getIdUser())).orElse(null);

        if (trainerDetails != null) {
            trainerDetails.setWorkArea(workArea);
            trainerDetailsRepository.save(trainerDetails);
        }

        return "redirect:/trainer/profile";
    }


}
