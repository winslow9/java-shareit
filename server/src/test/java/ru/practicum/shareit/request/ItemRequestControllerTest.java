package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void getItemRequests() throws Exception {
        Long userId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужна дрель");
        requestDto.setIdRequestor(userId);
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getAllByUserId(userId))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"))
                .andExpect(jsonPath("$[0].idRequestor").value(userId))
                .andExpect(jsonPath("$[0].created").exists());

        verify(itemRequestService).getAllByUserId(userId);
    }

    @Test
    void getAllItemRequests() throws Exception {
        Long userId = 1L;
        Long anotherUserId = 2L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужна стремянка");
        requestDto.setIdRequestor(anotherUserId);
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getAllByOtherUsers(userId))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна стремянка"))
                .andExpect(jsonPath("$[0].idRequestor").value(anotherUserId))
                .andExpect(jsonPath("$[0].created").exists());

        verify(itemRequestService).getAllByOtherUsers(userId);
    }

    @Test
    void getItemRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(requestId);
        requestDto.setDescription("Нужен перфоратор");
        requestDto.setIdRequestor(userId);
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.findById(eq(requestId)))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Нужен перфоратор"))
                .andExpect(jsonPath("$.idRequestor").value(userId))
                .andExpect(jsonPath("$.created").exists());

        verify(itemRequestService).findById(eq(requestId));
    }

    @Test
    void saveItemRequest() throws Exception {
        Long userId = 1L;

        ItemRequestDto requestToSave = new ItemRequestDto();
        requestToSave.setDescription("Нужна дрель");

        ItemRequestDto savedRequest = new ItemRequestDto();
        savedRequest.setId(1L);
        savedRequest.setDescription("Нужна дрель");
        savedRequest.setIdRequestor(userId);
        savedRequest.setCreated(LocalDateTime.now());

        when(itemRequestService.save(any(ItemRequestDto.class), eq(userId)))
                .thenReturn(savedRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.idRequestor").value(userId))
                .andExpect(jsonPath("$.created").exists());

        verify(itemRequestService).save(any(ItemRequestDto.class), eq(userId));
    }
}