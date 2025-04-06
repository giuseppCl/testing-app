package com.gcl.testingapp.controller;

import com.gcl.testingapp.security.SecurityConfig;
import com.gcl.testingapp.service.PostService;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    @DisplayName("Successfully: POST with ROLE_CREATOR")
    void postWithCreatorRole_shouldSucceed() throws Exception {
        when(postService.create(anyString())).thenReturn("Created post: Test");

        mockMvc.post()
                .uri("/posts")
                .content("test")
                .contentType(MediaType.TEXT_PLAIN)
                .with(csrf())
                .with(httpBasic("creator", "password"))
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .hasContentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .bodyText().isEqualTo("Created post: Test");
    }

    @Test
    @DisplayName("FAILURE: ROLE_USER is not allowed to create a post")
    void createPost_forbiddenForUserRole() throws Exception {
        mockMvc.post().uri("/posts")
                .content("test")
                .contentType(MediaType.TEXT_PLAIN)
                .with(csrf())
                .with(httpBasic("user", "password"))
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("No User → 401 Unauthorized")
    void createPost_unauthorizedWithoutToken() throws Exception {
        mockMvc.post().uri("/posts")
                .content("test")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }


    @Test
    @DisplayName("Wrong Content-Type → 415 Unsupported Media Type")
    void createPost_unsupportedMediaType() throws Exception {
        mockMvc.post().uri("/posts")
                .content(new JSONObject().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(httpBasic("creator", "password"))
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
