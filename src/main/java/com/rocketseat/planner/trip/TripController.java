package com.rocketseat.planner.trip;


import com.rocketseat.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private ParticipantService participantService;
    @Autowired
    private TripRepository repository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
        Trip newTRIP = new Trip(payload);
        this.repository.save(newTRIP);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(),newTRIP);

        return ResponseEntity.status(201).body(new TripCreateResponse(newTRIP.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetail(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);
        return trip.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id,@RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip updatedTrip = trip.get();

            updatedTrip.setDestination(payload.destination());
            updatedTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            updatedTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(),DateTimeFormatter.ISO_DATE_TIME));

            this.repository.save(updatedTrip);
            return ResponseEntity.status(201).body(updatedTrip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripConfirmed = trip.get();

            tripConfirmed.setIsConfirmed(true);

            this.repository.save(tripConfirmed);
            this.participantService.triggerConfirmationEmailToParticipants(tripConfirmed.getId());
            return ResponseEntity.ok(tripConfirmed);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripFounded = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToTrip(payload.email(),tripFounded);
            if(tripFounded.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataResponse>> listAllParticipants(@PathVariable UUID id){
       List<ParticipantDataResponse> participantList = this.participantService.listAllParticipantsToTrip(id);
       return ResponseEntity.ok(participantList);
    }

}
