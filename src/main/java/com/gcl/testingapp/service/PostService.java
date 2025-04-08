package com.gcl.testingapp.service;

import com.gcl.testingapp.exception.PostNotFoundException;
import com.gcl.testingapp.exception.UnauthorizedException;
import com.gcl.testingapp.model.Post;
import com.gcl.testingapp.model.User;
import com.gcl.testingapp.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserService userService;

    public Post create(String content, UserDetails userDetails) {
        User user = userService.getUserByName(userDetails.getUsername());
        Post post = new Post()
                .setContent(content)
                .setCreator(user);

        return postRepository.save(post);
    }

    public Post update(UUID postId, String content, UserDetails userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found:" + postId));
        User user = userService.getUserByName(userDetails.getUsername());

        if (!post.getCreator().getId().equals(user.getId())) {
            throw new UnauthorizedException("Unauthorized: Not permitted");
        }

        post.setContent(content);

        return postRepository.save(post);
    }


    public void testMethodeFueTransactional() {
        // do something with db
        // speicehre zwischen ergebnis var x = userService.update();
        testMethodeFueTransactional2();
    }

    public void testMethodeFueTransactional2() {
        // do something with db
        // speicehre zwischen ergebnis var y = postRepository.update();
    }
}
