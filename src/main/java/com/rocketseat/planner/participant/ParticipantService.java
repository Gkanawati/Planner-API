package com.rocketseat.planner.participant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.trip.Trip;

@Service
public class ParticipantService {

  @Autowired
  private ParticipantRepository repository;

  public void registerParticipantsToEvent(List<String> emails, Trip trip) {
    List<Participant> participants = emails.stream().map(email -> new Participant(email, trip)).toList();
    this.repository.saveAll(participants);
  }

  public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
    Participant newParticipant = new Participant(email, trip);
    this.repository.save(newParticipant);

    return new ParticipantCreateResponse(newParticipant.getId());
  }

  public void triggerConfirmationEmailToParticipants(UUID tripId) {
  }

  public ParticipantCreateResponse triggerConfirmationEmailToParticipant(String participantEmail) {
    return null;
  }

  public List<ParticipantData> getParticipantsByTripId(UUID tripId) {
    return this.repository.findByTripId(tripId).stream().map(participant -> new ParticipantData(participant.getId(),
        participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
  }
}
