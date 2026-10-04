package org.diplom_backend.mappers;

import org.diplom_backend.dto.requests.ChangeCommentRequestDto;
import org.diplom_backend.dto.requests.CreateCommentRequestDto;
import org.diplom_backend.dto.requests.CreateThreadRequestDto;
import org.diplom_backend.dto.responses.CommentResponseDto;
import org.diplom_backend.model.CommentEntity;
import org.diplom_backend.model.PublicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Named("emptyPublicationWithId")
    static PublicationEntity emptyPublicationWithId(Long id) {
        var publication = new PublicationEntity();
        publication.setId(id);
        return publication;
    }

    @Named("emptyCommentWithId")
    static CommentEntity emptyCommentWithId(Long id) {
        var comment = new CommentEntity();
        comment.setId(id);
        return comment;
    }

    @Mapping(target = "nickname", expression = "java(comment.getIsAnonymous() ? \"\" : comment.getAccount().getNickname())")
    CommentResponseDto toCommentResponseDto(CommentEntity comment);


    @Mapping(target = "thread", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publication", qualifiedByName = "emptyPublicationWithId", source = "publicationId")
    @Mapping(target = "account", ignore = true)
    CommentEntity fromCreateCommentRequestDto(CreateCommentRequestDto comment);

    @Mapping(target = "thread", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "isAnonymous", ignore = true)
    @Mapping(target = "publication", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "account", ignore = true)
    CommentEntity fromChangeCommentRequestDto(ChangeCommentRequestDto comment);

    @Mapping(target = "thread", ignore = true)
    @Mapping(target = "parent", qualifiedByName = "emptyCommentWithId", source = "parentCommentId")
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publication", ignore = true)
    @Mapping(target = "account", ignore = true)
    CommentEntity fromCreateThreadRequestDto(CreateThreadRequestDto comment);
}
