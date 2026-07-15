package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void getAllByUserId() {
        User requestor = new User();
        requestor.setName("Max");
        requestor.setEmail("max_request@mail.com");
        em.persist(requestor);

        User anotherUser = new User();
        anotherUser.setName("Anna");
        anotherUser.setEmail("anna_request@mail.com");
        em.persist(anotherUser);

        em.flush();

        itemRequestService.save(
                new ItemRequestDto(null, "Нужна стремянка", null, null, null),
                requestor.getId()
        );

        itemRequestService.save(
                new ItemRequestDto(null, "Нужен перфоратор", null, null, null),
                anotherUser.getId()
        );

        Collection<ItemRequestDto> result = itemRequestService.getAllByUserId(requestor.getId());

        assertThat(result, hasSize(1));

        ItemRequestDto foundRequest = result.iterator().next();

        assertThat(foundRequest.getDescription(), equalTo("Нужна стремянка"));
        assertThat(foundRequest.getIdRequestor(), equalTo(requestor.getId()));
    }
}