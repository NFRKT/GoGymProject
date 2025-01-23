//package com.GoGym.controller;
//
//import com.GoGym.module.BodyPart;
//import com.GoGym.service.BodyPartService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/bodyparts")
//public class BodyPartController {
//
//    private final BodyPartService bodyPartService;
//
//    public BodyPartController(BodyPartService bodyPartService) {
//        this.bodyPartService = bodyPartService;
//    }
//    @GetMapping
//    public List<BodyPart> getAllBodyParts() {
//        return bodyPartService.findAll();
//    }
//    @PostMapping
//    public BodyPart createBodyPart(@RequestBody BodyPart bodyPart) {
//        return bodyPartService.save(bodyPart);
//    }
//}
