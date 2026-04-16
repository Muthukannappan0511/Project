package com.rvz.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rvz.dto.employee.BookingCreateRequestDto;
import com.rvz.dto.employee.BookingDto;
import com.rvz.dto.employee.RoomSuggestionDto;
import com.rvz.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee")
public class EmployeeBookingController {

    private final BookingService service;

    public EmployeeBookingController(BookingService service) {
        this.service = service;
    }

    // ✅ Suggest available rooms
    @GetMapping("/rooms/suggest")
    public List<RoomSuggestionDto> suggestRooms(
            @RequestParam Long branchId,
            @RequestParam String date,
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String facilities
    ) {
        return service.suggestRooms(branchId, date, start, end, capacity, facilities);
    }

    // ✅ Create booking request
    @PostMapping("/bookings")
    public BookingDto createBooking(@Valid @RequestBody BookingCreateRequestDto dto, Authentication auth) {
        return service.createBooking(auth.getName(), dto);
    }

    // ✅ View my bookings
    @GetMapping("/bookings")
    public List<BookingDto> myBookings(Authentication auth) {
        return service.myBookings(auth.getName());
    }

    // ✅ Cancel booking (PENDING only)
    @DeleteMapping("/bookings/{id}")
    public String cancel(@PathVariable Long id, Authentication auth) {
        service.cancelBooking(auth.getName(), id);
        return "Cancelled";
    }
}