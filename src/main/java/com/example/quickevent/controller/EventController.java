package com.example.quickevent.controller;

import com.example.quickevent.model.Event;
import com.example.quickevent.model.User;
import com.example.quickevent.repository.UserRepository;
import com.example.quickevent.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    // ✅ View all events created by the logged-in user
    @GetMapping
    public String listUserEvents(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);

        List<Event> events = eventService.getEventsByUser(user);
        model.addAttribute("events", events);

        return "event_list"; // ✅ Will show all events for that user
    }

    // ✅ Show create event form
    @GetMapping("/new")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "create_event"; // ✅ HTML page for adding new event
    }

    // ✅ Handle form submission for creating event
    @PostMapping("/save")
    public String saveEvent(@ModelAttribute Event event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            event.setUser(user); // Set the creator of the event
        }

        eventService.saveEvent(event);
        return "redirect:/events"; // ✅ Redirect to event list
    }

    // ✅ Delete event
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }

    // ✅ Edit event
    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "edit_event";
    }

    // ✅ Update event
    @PostMapping("/update")
    public String updateEvent(@ModelAttribute Event event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            event.setUser(user);
        }

        eventService.saveEvent(event);
        return "redirect:/events";
    }
}
