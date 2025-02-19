package com.GoGym.controller;

import com.GoGym.module.User;
import com.GoGym.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")

public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/panel")
    public String adminPanel(Model model) {
        List<User> clients = userService.getUsersByType(User.UserType.CLIENT);
        List<User> trainers = userService.getUsersByType(User.UserType.TRAINER);

        model.addAttribute("clients", clients);
        model.addAttribute("trainers", trainers);
        return "admin-panel";
    }

    @DeleteMapping("/admin/delete-user/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
