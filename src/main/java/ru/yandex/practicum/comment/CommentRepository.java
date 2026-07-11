package ru.yandex.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemIdOrderByCreatedAsc(Long itemId);

    List<Comment> findByAuthorId(Long authorId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemIds ORDER BY c.item.id, c.created ASC")
    List<Comment> findByItemIdsOrderByCreatedAsc(@Param("itemIds") List<Long> itemIds);
}