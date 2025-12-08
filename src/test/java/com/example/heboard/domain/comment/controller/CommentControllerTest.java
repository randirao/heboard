package com.example.heboard.domain.comment.controller;

import com.example.heboard.domain.comment.exception.CommentForbiddenException;
import com.example.heboard.domain.comment.exception.CommentNotFoundException;
import com.example.heboard.domain.comment.service.CommentService;
import com.example.heboard.security.JwtUserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    private void setAuthentication(JwtUserPrincipal principal) {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() throws Exception {
        setAuthentication(new JwtUserPrincipal(1L, "user"));

        mockMvc.perform(delete("/api/comments/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("댓글이 성공적으로 삭제되었습니다."));

        Mockito.verify(commentService).deleteComment(10L, 1L);
    }

    @Test
    @DisplayName("댓글 삭제 - 작성자 불일치 403")
    void deleteComment_forbidden() throws Exception {
        setAuthentication(new JwtUserPrincipal(2L, "other"));
        Mockito.doThrow(new CommentForbiddenException()).when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/api/comments/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    @DisplayName("댓글 삭제 - 존재하지 않음 404")
    void deleteComment_notFound() throws Exception {
        setAuthentication(new JwtUserPrincipal(1L, "user"));
        Mockito.doThrow(new CommentNotFoundException()).when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/api/comments/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("COMMENT_NOT_FOUND"));
    }

    @Test
    @DisplayName("댓글 삭제 - 인증 없음 401")
    void deleteComment_unauthorized() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(delete("/api/comments/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }
}
