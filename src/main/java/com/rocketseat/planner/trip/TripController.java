package com.rocketseat.planner.trip;

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
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/trips")
public class TripController {
  @Autowired
  private TripService tripService;

  @PostMapping("")
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) throws Exception {
    return ResponseEntity.ok(tripService.createTrip(payload));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
    return ResponseEntity.ok(tripService.getTripDetails(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload) {
    return ResponseEntity.ok(tripService.updateTrip(id, payload));
  }

  @GetMapping("/{id}/confirm")
  public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
    return ResponseEntity.ok(tripService.confirmTrip(id));
  }

  @PostMapping("/{id}/invite")
  public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,
      @RequestBody ParticipantRequestPayload payload) {
    return ResponseEntity.ok(tripService.inviteParticipant(id, payload));
  }

  @GetMapping("/{id}/participants")
  public ResponseEntity<List<ParticipantData>> getTripParticipants(@PathVariable UUID id) {
    return ResponseEntity.ok(tripService.getTripParticipants(id));
  }

  @PostMapping("/{id}/activities")
  public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id,
      @RequestBody ActivityRequestPayload payload) {
    return ResponseEntity.ok(tripService.registerActivity(id, payload));
  }

  @GetMapping("/{id}/activities")
  public ResponseEntity<List<ActivityData>> getTripActivities(@PathVariable UUID id) {
    return ResponseEntity.ok(tripService.getTripActivities(id));
  }

  @PostMapping("/{id}/links")
  public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id,
      @RequestBody LinkRequestPayload payload) {
    return ResponseEntity.ok(tripService.registerLink(id, payload));
  }

  @GetMapping("{id}/links")
  public ResponseEntity<List<LinkData>> getTripLinks(@PathVariable UUID id) {
    return ResponseEntity.ok(tripService.getTripLinks(id));
  }
}
