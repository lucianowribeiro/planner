package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {
    @Autowired
    ActivityRespository activityRespository;

    public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip){
        Activity activity = new Activity(payload.title(),payload.occursAt(),trip);
        this.activityRespository.save(activity);
        return new ActivityResponse(activity.getId());
    }
    public List<ActivityDataResponse> listAllActivities(UUID tripId){
        return this.activityRespository.findByTripId(tripId).stream().map(activity -> new ActivityDataResponse(activity.getId(),activity.getTitle(),activity.getOccursAt())).toList();
    }
}
