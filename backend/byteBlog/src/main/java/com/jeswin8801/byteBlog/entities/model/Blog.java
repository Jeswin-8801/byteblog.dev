package com.jeswin8801.byteBlog.entities.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String heading;

    @Column(nullable = false)
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

    @OneToOne(
            mappedBy = "blog",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private User user;

    @OneToMany(
            mappedBy = "blog",
            fetch = FetchType.LAZY
    )
    private Set<Comment> comments = new HashSet<>();
}
