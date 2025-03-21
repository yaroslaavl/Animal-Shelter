package org.shelter.app.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.enums.PetStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "webapp",name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(name = "breed", nullable = false)
    private String breed;

    @Column(unique = true, name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

