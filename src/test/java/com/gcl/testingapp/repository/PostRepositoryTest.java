package com.gcl.testingapp.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.gcl.testingapp.model.Post;
import com.gcl.testingapp.model.User;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class PostRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

  @Autowired
  PostRepository postRepository;

  @Autowired
  UserRepository userRepository;

  @Test
  @DisplayName("Connection: PostgreSQL container created successfully")
  void connectionEstablished() {
    assertThat(postgres.isCreated()).isTrue();
  }

  @Test
  @DisplayName("Create and find Post by ID")
  public void testCreateAndFindPost() {
    User user = new User();
    user.setName("Test User");
    user = userRepository.save(user);

    Post post = new Post();
    post.setContent("Test Post");
    post.setCreator(user);
    post = postRepository.save(post);

    Optional<Post> retrievedPost = postRepository.findById(post.getId());
    Assertions.assertThat(retrievedPost).isPresent();
    Assertions.assertThat(retrievedPost.get().getContent()).isEqualTo("Test Post");
    Assertions.assertThat(retrievedPost.get().getCreator()).isEqualTo(user);
  }

  @Test
  @DisplayName("Update Post and verify changes")
  public void testUpdatePost() {
    User user = new User();
    user.setName("Test User");
    user = userRepository.save(user);

    Post post = new Post();
    post.setContent("Original Content");
    post.setCreator(user);
    post = postRepository.save(post);

    post.setContent("Updated Content");
    postRepository.save(post);

    Optional<Post> updatedPost = postRepository.findById(post.getId());
    Assertions.assertThat(updatedPost).isPresent();
    Assertions.assertThat(updatedPost.get().getContent()).isEqualTo("Updated Content");
  }

  @Test
  @DisplayName("Delete Post and verify it is not found")
  public void testDeletePost() {
    User user = new User();
    user.setName("Test User");
    user = userRepository.save(user);

    Post post = new Post();
    post.setContent("Post to delete");
    post.setCreator(user);
    post = postRepository.save(post);

    postRepository.delete(post);

    Optional<Post> deletedPost = postRepository.findById(post.getId());
    Assertions.assertThat(deletedPost).isEmpty();
  }
}
