package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void save() {
        User requestor = createUser(1L);
        ItemRequestDto requestDto = createItemRequestDto(null, "Нужна дрель", null, null);
        ItemRequest savedRequest = createItemRequest(1L, "Нужна дрель", requestor);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.save(requestDto, 1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getIdRequestor()).isEqualTo(1L);
        assertThat(result.getCreated()).isNotNull();

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void saveWhenUserNotFound() {
        ItemRequestDto requestDto = createItemRequestDto(null, "Нужна дрель", null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.save(requestDto, 1L));

        verify(userRepository).findById(1L);
    }

    @Test
    void getAllByUserId() {
        User requestor = createUser(1L);
        ItemRequest request = createItemRequest(1L, "Нужна дрель", requestor);
        Item item = createItem(1L, "Дрель", request);

        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        List<ItemRequestDto> result = itemRequestService.getAllByUserId(1L).stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.get(0).getIdRequestor()).isEqualTo(1L);

        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(1L);
        verify(itemRepository).findAllByRequestId(1L);
    }

    @Test
    void getAllByOtherUsers() {
        User requestor = createUser(2L);
        ItemRequest request = createItemRequest(1L, "Нужна дрель", requestor);
        Item item = createItem(1L, "Дрель", request);

        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L)));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllByOtherUsers(1L).stream().toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.get(0).getIdRequestor()).isEqualTo(2L);

        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findAllByRequestorIdNotOrderByCreatedDesc(1L);
        verify(itemRepository).findAllByRequestId(1L);
    }

    @Test
    void getAllByOtherUsersWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByOtherUsers(1L));

        verify(userRepository).findById(1L);
    }


    @Test
    void getByIdWhenRequestNotFound() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(1L));

        verify(itemRequestRepository).findById(1L);
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        return user;
    }

    private ItemRequestDto createItemRequestDto(Long id,
                                                String description,
                                                Long idRequestor,
                                                LocalDateTime created) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setDescription(description);
        dto.setIdRequestor(idRequestor);
        dto.setCreated(created);
        return dto;
    }

    private ItemRequest createItemRequest(Long id, String description, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    private Item createItem(Long id, String name, ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(createUser(3L));
        item.setRequest(request);
        return item;
    }
}