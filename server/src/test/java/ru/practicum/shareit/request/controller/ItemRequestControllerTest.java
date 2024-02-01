package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;
import utils.ResourcePool;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static utils.ResourcePool.itemRequestController_getRequestById_itemResponseDto;
import static utils.ResourcePool.itemRequestController_getUsersItemRequests_itemResponseDto;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void createItemRequest_shouldCreateItem() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Description").build();
        ItemRequestDto createdItemRequestDto = ItemRequestDto.builder().id(1).description("Description")
                .requestorId(1).created(LocalDateTime.of(2023, 12, 13, 0, 0))
                .build();

        when(itemRequestService.createItemRequest(1, itemRequestDto)).thenReturn(createdItemRequestDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(objectMapper.writeValueAsString(itemRequestDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(createdItemRequestDto)));

        verify(itemRequestService, Mockito.times(1)).createItemRequest(1, itemRequestDto);
        verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getUsersItemRequests_shouldReturnItemRequests() {
        List<ItemResponseDto> itemRequestDtos =
                ResourcePool.read(itemRequestController_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });
        when(itemRequestService.getUsersItemRequests(1)).thenReturn(itemRequestDtos);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestDtos)));

        verify(itemRequestService, Mockito.times(1)).getUsersItemRequests(1);
        verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getRequestById_shouldReturnItemRequest() {
        ItemResponseDto itemResponseDto =
                ResourcePool.read(itemRequestController_getRequestById_itemResponseDto, ItemResponseDto.class);
        when(itemRequestService.getRequestById(1, 1)).thenReturn(itemResponseDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemResponseDto)));

        verify(itemRequestService, Mockito.times(1)).getRequestById(1, 1);
        verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getRequestById_shouldThrowExceptionWhenRequestNotFound() {
        when(itemRequestService.getRequestById(1, 1))
                .thenThrow(new ItemRequestNotFoundException("Item request with id 1 was not found"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(itemRequestService, Mockito.times(1)).getRequestById(1, 1);
        verifyNoMoreInteractions(itemRequestService);
    }

    @SneakyThrows
    @Test
    void getAllRequests_shouldReturnAllItemRequests() {
        List<ItemResponseDto> itemRequestDtos =
                ResourcePool.read(itemRequestController_getUsersItemRequests_itemResponseDto, new TypeReference<>() {
                });
        when(itemRequestService.getAllRequests(1, 0, 20)).thenReturn(itemRequestDtos);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?from=0&size=20")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestDtos)));

        verify(itemRequestService, Mockito.times(1)).getAllRequests(1, 0, 20);
        verifyNoMoreInteractions(itemRequestService);
    }
}
