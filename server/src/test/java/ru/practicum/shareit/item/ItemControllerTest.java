package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void getAllItems() throws Exception {

        ItemWithDatesDto itemDto = new ItemWithDatesDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);


        when(itemService.findAll(itemDto.getId())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemService).findAll(itemDto.getId());
    }

    @Test
    void getItemById() throws Exception {
        ItemWithDatesDto itemDto = new ItemWithDatesDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Аккумуляторная дрель");
        itemDto.setAvailable(true);

        when(itemService.findById(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService).findById(1L);
    }

    @Test
    void searchItems() throws Exception {
        String text = "дрель";

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);

        when(itemService.findByText(text)).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[0].description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(itemService).findByText(text);
    }

    @Test
    void saveItem() throws Exception {
        Long userId = 1L;

        ItemDto itemToSave = new ItemDto();
        itemToSave.setName("Дрель");
        itemToSave.setDescription("Аккумуляторная дрель");
        itemToSave.setAvailable(true);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Дрель");
        savedItem.setDescription("Аккумуляторная дрель");
        savedItem.setAvailable(true);

        when(itemService.save(any(ItemDto.class), eq(userId))).thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService).save(any(ItemDto.class), eq(userId));
    }

    @Test
    void updateItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        ItemDto itemToUpdate = new ItemDto();
        itemToUpdate.setName("Дрель обновлённая");
        itemToUpdate.setDescription("Новое описание");
        itemToUpdate.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName("Дрель обновлённая");
        updatedItem.setDescription("Новое описание");
        updatedItem.setAvailable(false);

        when(itemService.update(any(ItemDto.class), eq(userId), eq(itemId))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель обновлённая"))
                .andExpect(jsonPath("$.description").value("Новое описание"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService).update(any(ItemDto.class), eq(userId), eq(itemId));
    }

    @Test
    void saveComment() throws Exception {

        Long userId = 1L;
        Long itemId = 1L;

        String commentText = "Great item!";
        CommentDto commentDto = new CommentDto();
        commentDto.setText(commentText);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText(commentText);

        User author = new User();
        author.setId(userId);
        author.setName("Booker");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(itemId), eq(userId), any(CommentDto.class)))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value(commentText))
                .andExpect(jsonPath("$.author.name").value("Booker"))
                .andExpect(jsonPath("$.created").exists());
    }
}
