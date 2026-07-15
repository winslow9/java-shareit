package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Anton");
        user.setEmail("anton@mail.com");

        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Anton"))
                .andExpect(jsonPath("$[0].email").value("anton@mail.com"));

        verify(userService).getAll();
    }

    @Test
    void getUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Anton");
        user.setEmail("anton@mail.com");

        when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Anton"))
                .andExpect(jsonPath("$.email").value("anton@mail.com"));

        verify(userService).getById(1L);
    }

    @Test
    void saveUser() throws Exception {
        User userToSave = new User();
        userToSave.setName("Anton");
        userToSave.setEmail("anton@mail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Anton");
        savedUser.setEmail("anton@mail.com");

        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Anton"))
                .andExpect(jsonPath("$.email").value("anton@mail.com"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteById(1L);
    }

    @Test
    void updateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Anton");
        user.setEmail("anton@mail.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Anton Updated");
        updatedUser.setEmail("anton_updated@mail.com");

        when(userService.updateUser(1L, updatedUser)).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Anton Updated"))
                .andExpect(jsonPath("$.email").value("anton_updated@mail.com"));

        verify(userService).updateUser(1L, updatedUser);
    }
}