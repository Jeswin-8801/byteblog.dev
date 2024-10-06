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
    @Column(name = "blog_id")
    private long id;

    @ElementCollection
    @CollectionTable(name = "blog_images")
    private Set<String> images;

    @Column(nullable = false)
    private Set<String> tags;

    @Column(nullable = false)
    private String markdownFileUrl;

    @OneToMany(
            mappedBy = "blog",
            fetch = FetchType.LAZY
    )
    private Set<Comment> comments = new HashSet<>();
}
