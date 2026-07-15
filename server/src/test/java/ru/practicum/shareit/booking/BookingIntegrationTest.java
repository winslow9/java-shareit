package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void getWaitingStatus() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner_booking@mail.com");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker_booking@mail.com");
        em.persist(booker);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Аккумуляторная дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        em.flush();

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking savedBooking = bookingService.save(bookingCreateDto, booker.getId());

        Booking bookingFromDb = em.find(Booking.class, savedBooking.getId());

        assertThat(bookingFromDb.getId(), notNullValue());
        assertThat(bookingFromDb.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingFromDb.getBooker().getId(), equalTo(booker.getId()));
        assertThat(bookingFromDb.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(bookingFromDb.getStart(), equalTo(bookingCreateDto.getStart()));
        assertThat(bookingFromDb.getEnd(), equalTo(bookingCreateDto.getEnd()));
    }
}