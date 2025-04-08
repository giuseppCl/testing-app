package com.gcl.testingapp.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    String content;

    @ManyToOne(optional = false)
    private User creator;

    public UUID getId() {
        return id;
    }

    public Post setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public Post setCreator(User creator) {
        this.creator = creator;
        return this;
    }
}
