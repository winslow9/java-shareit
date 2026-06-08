package ru.yandex.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemIdOrderByCreatedAsc(Long itemId);

    List<Comment> findByAuthorId(Long authorId);
}