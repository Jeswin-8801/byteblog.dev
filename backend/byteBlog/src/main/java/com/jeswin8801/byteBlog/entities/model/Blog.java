package com.jeswin8801.byteBlog.entities.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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
    private ArrayList<String> images;

    @Column(nullable = false)
    private ArrayList<String> tags;

    @Column(nullable = false)
    private String markdownFileUrl;

    @OneToOne(
            mappedBy = "blog",
            orphanRemoval = true,
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Column(name = "first_comment")
    private Comment firstComment;
}
