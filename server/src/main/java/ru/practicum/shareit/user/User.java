package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public User() {
    }

    public User(Long id,
                String name,
                String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
