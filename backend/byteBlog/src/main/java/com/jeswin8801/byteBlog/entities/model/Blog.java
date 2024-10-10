package com.jeswin8801.byteBlog.entities.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = { "user", "comments" })
@ToString(exclude = { "user", "comments" })
@Table(name = "blog")
public class Blog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String heading;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private Set<String> tags;

    @Column(name = "primary-tag")
    private String primaryTag;

    @Column(nullable = false)
    private String markdownFileUrl;

    @ElementCollection
    @CollectionTable(name = "blog_images")
    private Set<String> images;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "blog",
            fetch = FetchType.LAZY
    )
    private Set<Comment> comments;
}
