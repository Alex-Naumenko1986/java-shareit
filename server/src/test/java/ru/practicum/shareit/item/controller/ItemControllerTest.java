package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IllegalAddCommentOperationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import utils.ResourcePool;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static utils.ResourcePool.itemController_getItemById_itemDto;
import static utils.ResourcePool.itemController_getOwnersItems_itemDto;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder().name("Item 1").description("Item 1 description").available(true).build();
    }

    @SneakyThrows
    @Test
    void createItem_shouldCreateItem() {
        ItemDto createdItemDto = ItemDto.builder().id(1).name("Item 1").description("Item 1 description")
                .available(true).build();
        when(itemService.createItem(1, itemDto)).thenReturn(createdItemDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items")
                                .content(objectMapper.writeValueAsString(itemDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(createdItemDto)));

        verify(itemService, Mockito.times(1)).createItem(1, itemDto);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void updateItem_shouldUpdateItem() {
        ItemDto updatedItemDtoNoId = ItemDto.builder().name("Item 1 updated").description("Item 1 updated description")
                .available(true).build();
        ItemDto updatedItemDto = ItemDto.builder().id(1).name("Item 1 updated")
                .description("Item 1 updated description").available(true).build();
        when(itemService.updateItem(1, updatedItemDto)).thenReturn(updatedItemDto);


        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .content(objectMapper.writeValueAsString(updatedItemDtoNoId))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedItemDto)));

        verify(itemService, Mockito.times(1)).updateItem(1, updatedItemDto);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void updateItem_shouldThrowExceptionWhenItemNotFound() {
        ItemDto updatedItemDtoNoId = ItemDto.builder().name("Item 1 updated").description("Item 1 updated description")
                .available(true).build();
        ItemDto updatedItemDto = ItemDto.builder().id(1).name("Item 1 updated")
                .description("Item 1 updated description").available(true).build();
        when(itemService.updateItem(1, updatedItemDto)).thenThrow(new ItemNotFoundException("Item with " +
                "id 1 was not found"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .content(objectMapper.writeValueAsString(updatedItemDtoNoId))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(itemService, Mockito.times(1)).updateItem(1, updatedItemDto);
        verifyNoMoreInteractions(itemService);
    }


    @SneakyThrows
    @Test
    void getItemById_shouldReturnItem() {
        ItemDto itemDto1 = ResourcePool.read(itemController_getItemById_itemDto, ItemDto.class);
        when(itemService.getItemById(1, 1)).thenReturn(itemDto1);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/1")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemDto1)));

        verify(itemService, Mockito.times(1)).getItemById(1, 1);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void getOwnersItems_shouldReturnItems() {
        List<ItemDto> itemDtos = ResourcePool.read(itemController_getOwnersItems_itemDto, new TypeReference<>() {
        });
        when(itemService.getOwnersItems(1, 0, 20)).thenReturn(itemDtos);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items?from=0&size=20")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemDtos)));

        verify(itemService, Mockito.times(1)).getOwnersItems(1, 0, 20);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void searchItems_shouldReturnItem() {
        itemDto.setId(1);
        when(itemService.searchItems("item", 0, 20)).thenReturn(List.of(itemDto));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search?text=item&from=0&size=20")
                                .header("X-Sharer-User-Id", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of(itemDto))));

        verify(itemService, Mockito.times(1)).searchItems("item", 0, 20);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void addComment_shouldAddNewComment() {
        CommentDto commentDto = CommentDto.builder().text("Comment 1").build();
        CommentDto addedCommentDto = CommentDto.builder().id(1).authorName("User 1").text("Comment 1")
                .created(LocalDateTime.of(2023, 12, 6, 19, 30)).build();
        when(itemService.addComment(1, 1, commentDto)).thenReturn(addedCommentDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(commentDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(addedCommentDto)));

        verify(itemService, Mockito.times(1)).addComment(1, 1, commentDto);
        verifyNoMoreInteractions(itemService);
    }

    @SneakyThrows
    @Test
    void addComment_shouldThrowExceptionWhenUserDidNotBookItem() {
        CommentDto commentDto = CommentDto.builder().text("Comment 1").build();
        when(itemService.addComment(1, 1, commentDto))
                .thenThrow(new IllegalAddCommentOperationException("User with id: 1 can't add comments to " +
                        "item with id: 1. This user didn't book this item"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(commentDto))
                                .header("X-Sharer-User-Id", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(itemService, Mockito.times(1)).addComment(1, 1, commentDto);
        verifyNoMoreInteractions(itemService);
    }
}
