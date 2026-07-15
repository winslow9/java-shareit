package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
