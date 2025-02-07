package com.GoGym.module;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name = "trainer_experience")
public class TrainerExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_trainer", nullable = false)
    private TrainerDetails trainer;

    @Column(name = "graduation_name", nullable = false)
    private String graduationName;

    @Column(name = "graduation_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date graduationDate;

    @Column(name = "certification_file")
    private String certificationFile;

    public TrainerExperience() {}

    public TrainerExperience(TrainerDetails trainer, String graduationName, Date graduationDate, String certificationFile) {
        this.trainer = trainer;
        this.graduationName = graduationName;
        this.graduationDate = graduationDate;
        this.certificationFile = certificationFile;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TrainerDetails getTrainer() { return trainer; }
    public void setTrainer(TrainerDetails trainer) { this.trainer = trainer; }

    public String getGraduationName() { return graduationName; }
    public void setGraduationName(String graduationName) { this.graduationName = graduationName; }

    public Date getGraduationDate() { return graduationDate; }
    public void setGraduationDate(Date graduationDate) { this.graduationDate = graduationDate; }

    public String getCertificationFile() { return certificationFile; }
    public void setCertificationFile(String certificationFile) { this.certificationFile = certificationFile; }
}
