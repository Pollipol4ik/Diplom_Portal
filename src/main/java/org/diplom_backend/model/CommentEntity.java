package org.diplom_backend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Комментарий
 */
@Entity
@Table(name = "comment")
@Audited
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentEntity {
    /**
     * Идентификатор комментария
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Содержание комментария
     */
    @Column(name = "content")
    private String content;

    @NotAudited
    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    /**
     * Дата создания комментария
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    /**
     * Пост, к которому комментарий был написан
     */
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", referencedColumnName = "id")
    private PublicationEntity publication;

    @NotAudited
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @NotAudited
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<CommentEntity> thread;

    @NotAudited
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent", referencedColumnName = "id")
    private CommentEntity parent;
}
