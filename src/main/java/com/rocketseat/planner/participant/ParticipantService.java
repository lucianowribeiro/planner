package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    ParticipantRepository participantRepository;

    public void registerParticipantsToTrip(List<String> participantsToInvite, Trip trip){
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email,trip)).toList();

        this.participantRepository.saveAll(participants);
    }
    public ParticipantCreateResponse registerParticipantToTrip(String emailToInvite ,Trip trip){
        Participant newParticipant = new Participant(emailToInvite,trip);
        this.participantRepository.save(newParticipant);
        return new ParticipantCreateResponse(newParticipant.getId());
    }
    public void triggerConfirmationEmailToParticipants(UUID tripId){}
    public void triggerConfirmationEmailToParticipant(String email){}
    public List<ParticipantDataResponse> listAllParticipantsToTrip(UUID tripId){
        return this.participantRepository.findByTripId(tripId).stream().map(participant -> new ParticipantDataResponse(participant.getId(),participant.getName(),participant.getEmail(),participant.getIsConfirmed())).toList();
    }

}
