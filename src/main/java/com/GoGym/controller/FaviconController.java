package com.GoGym.controller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Files;

@Controller
public class FaviconController {
    @GetMapping("/favicon.ico")
    @ResponseBody
    public void getFavicon(HttpServletResponse response) throws IOException {
        Resource resource = new ClassPathResource("static/favicon.ico");
        if (resource.exists()) {
            response.setContentType("image/x-icon");
            Files.copy(resource.getFile().toPath(), response.getOutputStream());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
