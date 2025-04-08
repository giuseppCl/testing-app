package com.gcl.testingapp.service;

import com.gcl.testingapp.exception.PostNotFoundException;
import com.gcl.testingapp.exception.UnauthorizedException;
import com.gcl.testingapp.model.Post;
import com.gcl.testingapp.model.User;
import com.gcl.testingapp.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @Mock
    private UserDetails userDetails;

    private final UUID postId = UUID.randomUUID();
    private final User mockUser = new User()
            .setId(UUID.randomUUID())
            .setName("testUser");

    private final Post existingPost = new Post()
            .setId(postId)
            .setContent("Old content")
            .setCreator(mockUser);

    @Test
    @DisplayName("Should create a new post successfully")
    void givenValidContent_whenCreatePost_thenPostIsCreated() {
        String content = "New post content";

        when(userService.getUserByName("testUser")).thenReturn(mockUser);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userDetails.getUsername()).thenReturn("testUser");

        Post result = postService.create(content, userDetails);

        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getCreator()).isEqualTo(mockUser);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("Should update post successfully when user is creator")
    void givenExistingPostAndAuthorizedUser_whenUpdatePost_thenPostIsUpdated() {
        String updatedContent = "Updated content";

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(userService.getUserByName("testUser")).thenReturn(mockUser);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userDetails.getUsername()).thenReturn("testUser");

        Post result = postService.update(postId, updatedContent, userDetails);

        assertThat(result.getId()).isEqualTo(postId);
        assertThat(result.getContent()).isEqualTo(updatedContent);
        verify(postRepository).save(existingPost);
    }

    @Test
    @DisplayName("Should throw exception when post does not exist")
    void givenNonExistingPost_whenUpdatePost_thenThrowException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.update(postId, "new content", userDetails))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("Post not found");
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not the post creator")
    void givenUnauthorizedUser_whenUpdatePost_thenThrowException() {
        User otherUser = new User().setId(UUID.randomUUID()).setName("anotherUser");
        Post otherPost = new Post()
                .setId(postId)
                .setContent("Not yours")
                .setCreator(otherUser);

        when(postRepository.findById(postId)).thenReturn(Optional.of(otherPost));
        when(userService.getUserByName("testUser")).thenReturn(mockUser);
        when(userDetails.getUsername()).thenReturn("testUser");


        assertThatThrownBy(() -> postService.update(postId, "new content", userDetails))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Unauthorized: Not permitted");
    }
}
