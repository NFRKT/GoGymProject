package com.GoGym.service;

import com.GoGym.module.BodyPart;
import com.GoGym.repository.BodyPartRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BodyPartService {

    private final BodyPartRepository bodyPartRepository;

    public BodyPartService(BodyPartRepository bodyPartRepository) {
        this.bodyPartRepository = bodyPartRepository;
    }

    public List<BodyPart> findAll() {
        return bodyPartRepository.findAll();
    }

    public BodyPart save(BodyPart bodyPart) {
        return bodyPartRepository.save(bodyPart);
    }
}
