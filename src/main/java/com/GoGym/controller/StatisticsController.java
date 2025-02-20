package com.GoGym.controller;

import com.GoGym.module.User;
import com.GoGym.security.CustomUserDetails;
import com.GoGym.service.StatisticsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public String getStatisticsPage(@RequestParam(required = false) Integer year,
                                    @RequestParam(required = false) Integer month,
                                    Model model,
                                    Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        LocalDate now = LocalDate.now();
        if (year == null) {
            year = now.getYear();
        }
        if (month == null) {
            month = now.getMonthValue();
        }

        Map<String, Object> statistics = statisticsService.calculateStatistics(user, year, month);

        // Pobranie poprawnej nazwy miesiąca w mianowniku (pełna nazwa samodzielna)
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("pl"));

        // Przekazanie mapy miesięcy do widoku
        Map<Integer, String> monthsMap = IntStream.rangeClosed(1, 12)
                .boxed()
                .collect(Collectors.toMap(m -> m, m -> Month.of(m).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("pl"))));

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("monthName", monthName);
        model.addAttribute("monthsMap", monthsMap);
        model.addAttribute("statistics", statistics);

        return "statistics";
    }

    @GetMapping("/update")
    @ResponseBody
    public Map<String, Object> updateStatistics(@RequestParam Integer year,
                                                @RequestParam Integer month,
                                                Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getUser();

        return statisticsService.calculateStatistics(user, year, month);
    }
}
