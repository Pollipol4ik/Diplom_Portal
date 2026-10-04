package org.diplom_backend.services;



import lombok.RequiredArgsConstructor;
import org.diplom_backend.model.CommentAudit;
import org.diplom_backend.repositories.CommentAudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentAudService {
    private final CommentAudRepository commentAudRepository;

    public List<CommentAudit> getRevisionForComment(Long commentId) {
        return commentAudRepository.getRevisionForComment(commentId);
    }
}
