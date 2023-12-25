package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") int bookerId,
                                    @RequestBody ShortBookingDto bookingDto) {
        bookingDto.setBookerId(bookerId);
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDto);
        log.info("Created new booking: {}", bookingDtoCreated);
        return bookingDtoCreated;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable("bookingId") Integer bookingId,
                                     @RequestParam("approved") Boolean isApproved) {
        return bookingService.approveBooking(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable("bookingId") Integer bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUsersBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return bookingService.getUsersBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnersBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "20") Integer size) {
        return bookingService.getOwnersBookings(userId, state, from, size);
    }
}
