package org.diplom_backend.mappers;

import org.diplom_backend.dto.responses.CourseGroupMemberResponseDto;
import org.diplom_backend.dto.responses.CourseGroupResponseDto;
import org.diplom_backend.model.CourseGroup;
import org.diplom_backend.model.CourseGroupMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseGroupMapper {

    public CourseGroupResponseDto toResponseDto(CourseGroup group) {
        List<CourseGroupMemberResponseDto> members = group.getMembers() == null
                ? List.of()
                : group.getMembers().stream().map(this::toMemberDto).toList();

        return new CourseGroupResponseDto(
                group.getId(),
                group.getCourse().getId(),
                group.getTitle(),
                group.getDescription(),
                group.getSchool().getId(),
                group.getSchool().getName(),
                members,
                group.getCreatedAt()
        );
    }

    public CourseGroupMemberResponseDto toMemberDto(CourseGroupMember member) {
        return new CourseGroupMemberResponseDto(
                member.getId(),
                member.getAccount().getId(),
                member.getAccount().getNickname(),
                member.getAccount().getFirstName(),
                member.getAccount().getLastName(),
                member.getIsOwner()
        );
    }
}
