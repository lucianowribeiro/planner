package com.rocketseat.planner.trip;


import com.rocketseat.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripPayload payload){
        Trip newTRIP = new Trip(payload);
        this.repository.save(newTRIP);
        this.participantService.registerParticipantToTrip(payload.emails_to_invite(),newTRIP.getId());

        return ResponseEntity.status(201).body(new TripCreateResponse(newTRIP.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> listTrips(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);
        return trip.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }
}
