package com.rocketseat.planner.trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Table(name = "trips")
@Entity
@Getter
@Setter
public class Trip {
    public Trip(){}

    public Trip(TripPayload payload){
        this.destination = payload.destination();
        this.startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        this.endsAt =  LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);
        this.isConfirmed = false;
        this.ownerName =  payload.owner_name();
        this.ownerEmail = payload.owner_email();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String destination;

    @Column(name = "starts_at",nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at",nullable = false)
    private LocalDateTime endsAt;

    @Column(name = "is_confirmed",nullable = false)
    private Boolean isConfirmed;

    @Column(name = "owner_name",nullable = false)
    private String ownerName;

    @Column(name = "owner_email",nullable = false)
    private String ownerEmail;


}
