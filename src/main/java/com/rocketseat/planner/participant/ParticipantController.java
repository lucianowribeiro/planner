package com.rocketseat.planner.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participant")
public class ParticipantController {

    @Autowired
    ParticipantRepository participantRepository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {

        Optional<Participant> participant = this.participantRepository.findById(id);
        if(participant.isPresent()){
            Participant participantConfirmed = participant.get();

            participantConfirmed.setIsConfirmed(true);
            participantConfirmed.setName(payload.name());
            participantConfirmed.setEmail(payload.email());

            this.participantRepository.save(participantConfirmed);

            return ResponseEntity.status(201).body(participantConfirmed);
        }
        return ResponseEntity.notFound().build();
    }
}
