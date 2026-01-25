package com.example;

import com.example.rest.EventResource;
import com.example.rest.NotificationResource;
import com.example.rest.PaymentResource;
import com.example.rest.ReservationResource;
import com.example.rest.TimeslotResource;
import com.example.rest.UserResource;
import com.example.rest.VenueResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(UserResource.class);
        classes.add(EventResource.class);
        classes.add(PaymentResource.class);
        classes.add(ReservationResource.class);
        classes.add(VenueResource.class);
        classes.add(TimeslotResource.class);
        classes.add(NotificationResource.class);
        return classes;
    }
}