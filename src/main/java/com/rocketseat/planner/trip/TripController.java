package com.rocketseat.planner.trip;
import com.rocketseat.planner.activity.ActivityDataResponse;
import com.rocketseat.planner.link.*;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;

import com.rocketseat.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;


    // trip endpoints
    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
        Trip newTRIP = new Trip(payload);
        this.repository.save(newTRIP);
        this.participantService.registerParticipantsToTrip(payload.emails_to_invite(),newTRIP);

        return ResponseEntity.status(201).body(new TripCreateResponse(newTRIP.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetail(@PathVariable UUID id){
        Optional<Trip> result = this.repository.findById(id);
        return result.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id,@RequestBody TripRequestPayload payload){
        Optional<Trip> result = this.repository.findById(id);

        if(result.isPresent()){
            Trip trip = result.get();

            trip.setDestination(payload.destination());
            trip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            trip.setEndsAt(LocalDateTime.parse(payload.ends_at(),DateTimeFormatter.ISO_DATE_TIME));

            this.repository.save(trip);
            return ResponseEntity.status(201).body(trip);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> result = this.repository.findById(id);

        if(result.isPresent()){
            Trip trip = result.get();

            trip.setIsConfirmed(true);

            this.repository.save(trip);
            this.participantService.triggerConfirmationEmailToParticipants(trip.getId());
            return ResponseEntity.ok(trip);
        }

        return ResponseEntity.notFound().build();
    }

    //activity endpoints
    @PostMapping("/{id}/activity")
    public ResponseEntity<ActivityResponse> saveActivity(@PathVariable UUID id ,@RequestBody ActivityRequestPayload payload){
        Optional<Trip> result = this.repository.findById(id);

        if(result.isPresent()){
            Trip trip = result.get();
            ActivityResponse activityResponse = this.activityService.registerActivity(payload,trip);
            return ResponseEntity.status(201).body(activityResponse);

        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDataResponse>> listAllActivities(@PathVariable UUID id){
        List<ActivityDataResponse> activitiesList = this.activityService.listAllActivities(id);
        return ResponseEntity.ok(activitiesList);
    }

    //participant endpoints
    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Trip> result = this.repository.findById(id);

        if(result.isPresent()){
            Trip trip = result.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToTrip(payload.email(),trip);
            if(trip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDataResponse>> listAllParticipants(@PathVariable UUID id){
       List<ParticipantDataResponse> participantList = this.participantService.listAllParticipantsToTrip(id);
       return ResponseEntity.ok(participantList);
    }

    //link endpoints
    @PostMapping("/{id}/link")
    public ResponseEntity<LinkResponse> saveLink(@PathVariable UUID id ,@RequestBody LinkRequestPayload payload){
        Optional<Trip> result = this.repository.findById(id);

        if(result.isPresent()){
            Trip trip = result.get();
            LinkResponse linkResponse = this.linkService.registerLink(payload,trip);
            return ResponseEntity.status(201).body(linkResponse);

        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkDataResponse>> listAllLinks(@PathVariable UUID id){
        List<LinkDataResponse> linksList = this.linkService.listAllLinks(id);
        return ResponseEntity.ok(linksList);
    }
}
