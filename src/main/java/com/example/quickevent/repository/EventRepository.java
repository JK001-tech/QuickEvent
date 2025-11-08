package com.example.quickevent.repository;

import com.example.quickevent.model.Event;
import com.example.quickevent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Custom query to find events created by a specific user
    List<Event> findByUser(User user);
}
