package org.shelter.app.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.shelter.app.database.entity.enums.AdoptionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(schema = "webapp", name = "adoption_request")
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", referencedColumnName = "id", nullable = false)
    private Pet pet;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AdoptionStatus adoptionStatus;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
