package com.jeswin8801.byteBlog.entities.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;

    @Column(nullable = false)
    private String comment;

    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @OneToOne(
            mappedBy = "comments",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Column(name = "next_comment")
    private Comment nextComment;

    @Column(name = "reply_comment")
    private Comment replyComment;
}
