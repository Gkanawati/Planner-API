package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.exception.ResourceNotFoundException;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/trips")
public class TripController {

  @Autowired // -> Autowired eh uma anotacao que faz a injecao de dependencia do Spring
  private ParticipantService participantService;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private LinkService linkService;

  @Autowired
  private TripRepository repository;

  @PostMapping("")
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) throws Exception {
    LocalDateTime startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

    if (startsAt.isAfter(endsAt)) {
      throw new Exception("The start date must be before the end date");
    }

    Trip newTrip = new Trip(payload);

    this.repository.save(newTrip);
    this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

    return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
    Trip trip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    return ResponseEntity.ok(trip);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
    Trip rawTrip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    rawTrip.setDestination(payload.destination());
    rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
    rawTrip.setEndsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));

    this.repository.save(rawTrip);

    return ResponseEntity.ok(rawTrip);
  }

  @GetMapping("/{id}/confirm")
  public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
    Trip rawTrip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    rawTrip.setIsConfirmed(true);
    this.repository.save(rawTrip);

    this.participantService.triggerConfirmationEmailToParticipants(id);

    return ResponseEntity.ok(rawTrip);
  }

  @PostMapping("/{id}/invite")
  public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
      @RequestBody ParticipantRequestPayload payload) {
    Trip rawTrip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    ParticipantCreateResponse participantCreateResponse = this.participantService
        .registerParticipantToEvent(payload.email(), rawTrip);

    if (rawTrip.getIsConfirmed()) {
      this.participantService.triggerConfirmationEmailToParticipant(payload.email());
    }

    return ResponseEntity.ok(participantCreateResponse);
  }

  @GetMapping("/{id}/participants")
  public ResponseEntity<List<ParticipantData>> getTripParticipants(@PathVariable UUID id) {
    this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    return ResponseEntity.ok(this.participantService.getParticipantsByTripId(id));
  }

  @PostMapping("/{id}/activities")
  public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id,
      @RequestBody ActivityRequestPayload payload) {
    Trip rawTrip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    LocalDateTime occursAt = LocalDateTime.parse(payload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime startsAt = rawTrip.getStartsAt();
    LocalDateTime endsAt = rawTrip.getEndsAt();

    if (occursAt.isBefore(startsAt) || occursAt.isAfter(endsAt)) {
      throw new IllegalArgumentException("The activity date must be between the trip start and end dates");
    }

    ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

    return ResponseEntity.ok(activityResponse);
  }

  @GetMapping("/{id}/activities")
  public ResponseEntity<List<ActivityData>> getTripActivities(@PathVariable UUID id) {
    this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    return ResponseEntity.ok(this.activityService.getActivitiesByTripId(id));
  }

  @PostMapping("/{id}/links")
  public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id,
      @RequestBody LinkRequestPayload payload) {
    Trip rawTrip = this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

    return ResponseEntity.ok(linkResponse);
  }

  @GetMapping("{id}/links")
  public ResponseEntity<List<LinkData>> getTripLinks(@PathVariable UUID id) {
    this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

    return ResponseEntity.ok(this.linkService.getLinksByTripId(id));
  }
}
