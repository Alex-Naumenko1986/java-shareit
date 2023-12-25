package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.BookingByOwnerException;
import ru.practicum.shareit.booking.exception.InvalidBookingDatesException;
import ru.practicum.shareit.booking.exception.InvalidBookingOperationException;
import ru.practicum.shareit.booking.exception.InvalidUserApprovesBookingException;
import ru.practicum.shareit.booking.service.BookingService;
import utils.ResourcePool;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static utils.ResourcePool.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void createBooking_shouldCreateBooking() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().itemId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 12, 0, 0)).build();
        BookingDto bookingDto = ResourcePool.read(bookingController_createBooking_bookingDto, BookingDto.class);
        when(bookingService.createBooking(any())).thenReturn(bookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings")
                                .content(objectMapper.writeValueAsString(shortBookingDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService, Mockito.times(1)).createBooking(any());
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void createBooking_shouldThrowExceptionWhenBookedByOwner() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().itemId(1)
                .start(LocalDateTime.of(2024, 2, 1, 0, 0))
                .end(LocalDateTime.of(2024, 2, 12, 0, 0)).build();
        when(bookingService.createBooking(any())).thenThrow(new BookingByOwnerException("Trying to book item " +
                "with id: 1 by the owner with id: 1"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings")
                                .content(objectMapper.writeValueAsString(shortBookingDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(bookingService, Mockito.times(1)).createBooking(any());
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void createBooking_shouldThrowExceptionWhenDatesOfBookingAreInvalid() {
        ShortBookingDto shortBookingDto = ShortBookingDto.builder().itemId(1)
                .start(LocalDateTime.of(2024, 2, 12, 0, 0))
                .end(LocalDateTime.of(2024, 2, 1, 0, 0)).build();
        when(bookingService.createBooking(any())).thenThrow(new InvalidBookingDatesException("Invalid booking " +
                "time of booking. End time should go after start time"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings")
                                .content(objectMapper.writeValueAsString(shortBookingDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(bookingService, Mockito.times(1)).createBooking(any());
        verifyNoMoreInteractions(bookingService);
    }


    @SneakyThrows
    @Test
    void approveBooking_shouldApproveBooking() {
        BookingDto bookingDto = ResourcePool.read(bookingController_approveBooking_bookingDto, BookingDto.class);
        when(bookingService.approveBooking(1, 1, true)).thenReturn(bookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService, Mockito.times(1)).approveBooking(1, 1, true);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void approveBooking_shouldThrowExceptionWhenBookingStatusIsNotWaiting() {
        when(bookingService.approveBooking(1, 1, true))
                .thenThrow(new InvalidBookingOperationException("Trying to update status of booking with id: 1." +
                        "Status of this booking is not WAITING and can't be changed"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(bookingService, Mockito.times(1)).approveBooking(1, 1, true);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void approveBooking_shouldThrowExceptionWhenInvalidUserApprovesBooking() {
        when(bookingService.approveBooking(1, 1, true))
                .thenThrow(new InvalidUserApprovesBookingException("User with id: 1 is trying to change status " +
                        "of booking with id: 1. Operation failed. User is not the owner of item"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(bookingService, Mockito.times(1)).approveBooking(1, 1, true);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getBooking_shouldReturnBooking() {
        BookingDto bookingDto = ResourcePool.read(bookingController_createBooking_bookingDto, BookingDto.class);
        when(bookingService.getBooking(1, 1)).thenReturn(bookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService, Mockito.times(1)).getBooking(1, 1);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getBooking_shouldThrowExceptionWhenBookingNotFound() {
        when(bookingService.getBooking(1, 1)).thenThrow(new BookingByOwnerException("Booking with " +
                "id 1 was not found"));


        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(bookingService, Mockito.times(1)).getBooking(1, 1);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getUsersBookings_shouldReturnAllBookings() {
        List<BookingDto> bookings = ResourcePool.read(bookingController_getUsersBookings_bookingDto,
                new TypeReference<>() {
                });
        when(bookingService.getUsersBookings(1, "ALL", 0, 20)).thenReturn(bookings);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, Mockito.times(1)).getUsersBookings(1, "ALL", 0, 20);
        verifyNoMoreInteractions(bookingService);
    }

    @SneakyThrows
    @Test
    void getOwnersBookings_shouldReturnAllBookings() {
        List<BookingDto> bookings = ResourcePool.read(bookingController_getUsersBookings_bookingDto,
                new TypeReference<>() {
                });
        when(bookingService.getOwnersBookings(2, "ALL", 0, 20)).thenReturn(bookings);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner")
                                .header("X-Sharer-User-Id", 2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));

        verify(bookingService, Mockito.times(1)).getOwnersBookings(2, "ALL", 0, 20);
        verifyNoMoreInteractions(bookingService);
    }
}
