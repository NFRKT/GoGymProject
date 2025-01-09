package com.GoGym.content_GoGym;
import com.GoGym.Module.*;
import com.GoGym.Service.userService;
import com.GoGym.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.time.LocalDateTime;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller

@ComponentScan({ "com.GoGym.*" })
public class ControlerGoGymHome {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    @Lazy
    private userService UserService;
    @Autowired
    private userRepository UserRepository;
    private double minus = 0;
    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Login zalogowanego użytkownika
        model.addAttribute("username", username);
        return "home";
    }
    @Scheduled(cron = "0 0 0 1 * ?")
    public void executeMonthlyTask() {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        user loggedInUser = UserRepository.findByEmail(currentUsername);
        if (loggedInUser == null) {
            throw new IllegalStateException("Nie znaleziono zalogowanego użytkownika");
        }


    }
}
