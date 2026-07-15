package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void getUserItems() {
        User owner = new User();
        owner.setName("Max");
        owner.setEmail("max-item-owner@mail.com");
        em.persist(owner);

        User anotherUser = new User();
        anotherUser.setName("Anna");
        anotherUser.setEmail("anna-item-owner@mail.com");
        em.persist(anotherUser);

        Item ownerItem = new Item();
        ownerItem.setName("Дрель");
        ownerItem.setDescription("Аккумуляторная дрель");
        ownerItem.setAvailable(true);
        ownerItem.setOwner(owner);
        em.persist(ownerItem);

        Item anotherUserItem = new Item();
        anotherUserItem.setName("Пила");
        anotherUserItem.setDescription("Циркулярная пила");
        anotherUserItem.setAvailable(true);
        anotherUserItem.setOwner(anotherUser);
        em.persist(anotherUserItem);

        em.flush();

        Collection<ItemWithDatesDto> result = itemService.findAll(owner.getId());

        assertThat(result, hasSize(1));

        ItemWithDatesDto itemDto = result.iterator().next();

        assertThat(itemDto.getId(), equalTo(ownerItem.getId()));
        assertThat(itemDto.getName(), equalTo("Дрель"));
        assertThat(itemDto.getDescription(), equalTo("Аккумуляторная дрель"));
        assertThat(itemDto.getAvailable(), equalTo(true));
    }
}