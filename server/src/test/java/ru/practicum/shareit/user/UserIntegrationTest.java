package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void getUser() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("Alex_test@mail.com");
        em.persist(user);

        em.flush();

        Collection<User> result = userService.getAll();

        assertThat(result, hasSize(1));

        User testUser = result.iterator().next();

        assertThat(testUser.getId(), equalTo(user.getId()));
        assertThat(testUser.getName(), equalTo(user.getName()));
        assertThat(testUser.getEmail(), equalTo(user.getEmail()));

    }
}
