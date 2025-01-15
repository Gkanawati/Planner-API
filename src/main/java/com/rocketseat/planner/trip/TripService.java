package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import com.rocketseat.planner.exception.ResourceNotFoundException;

@Service
public class TripService {

  @Autowired
  private TripRepository repository;

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private LinkService linkService;

  public TripCreateResponse createTrip(TripRequestPayload payload) {
    LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

    if (startsAt.isAfter(endsAt)) {
      throw new IllegalArgumentException("The start date must be before the end date");
    }

    Trip newTrip = new Trip(payload);
    repository.save(newTrip);
    participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);
    return new TripCreateResponse(newTrip.getId());
  }

  public Trip getTripDetails(UUID id) {
    return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
  }

  public Trip updateTrip(UUID id, TripRequestPayload payload) {
    Trip rawTrip = getTripDetails(id);
    rawTrip.setDestination(payload.destination());
    rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
    rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
    repository.save(rawTrip);
    return rawTrip;
  }

  public Trip confirmTrip(UUID id) {
    Trip trip = getTripDetails(id);
    trip.setIsConfirmed(true);
    repository.save(trip);
    participantService.triggerConfirmationEmailToParticipants(id);
    return trip;
  }

  public ParticipantCreateResponse inviteParticipant(UUID id, ParticipantRequestPayload payload) {
    Trip trip = getTripDetails(id);
    ParticipantCreateResponse response = participantService.registerParticipantToEvent(payload.email(), trip);
    if (trip.getIsConfirmed()) {
      participantService.triggerConfirmationEmailToParticipant(payload.email());
    }
    return response;
  }

  public List<ParticipantData> getTripParticipants(UUID id) {
    getTripDetails(id);
    return participantService.getParticipantsByTripId(id);
  }

  public ActivityResponse registerActivity(UUID id, ActivityRequestPayload payload) {
    Trip trip = getTripDetails(id);
    LocalDateTime occursAt = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);

    if (occursAt.isBefore(trip.getStartsAt()) || occursAt.isAfter(trip.getEndsAt())) {
      throw new IllegalArgumentException("The activity date must be between the trip start and end dates");
    }
    return activityService.registerActivity(payload, trip);
  }

  public List<ActivityData> getTripActivities(UUID id) {
    getTripDetails(id);
    return activityService.getActivitiesByTripId(id);
  }

  public LinkResponse registerLink(UUID id, LinkRequestPayload payload) {
    Trip trip = getTripDetails(id);
    return linkService.registerLink(payload, trip);
  }

  public List<LinkData> getTripLinks(UUID id) {
    getTripDetails(id);
    return linkService.getLinksByTripId(id);
  }
}