package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.security.CurrentUser;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommentController.class)
@WebMvcTest(controllers =  CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    private static final String ID_STR = "id";

    private static final String COMMENT_STR = "Comment";

    private static final String THIS_COMMENT = " this comment";

    private static final String YOU_DON_T_HAVE_PERMISSION_TO = "You don't have permission to ";


    @Test
    public void givenExistingComment_whenDeleteComment_thenIsOk() throws Exception {
        //given
        Long postId = 1L;
        Long existingCommentId = 3L;


        String existingUsername = "ervin";
        Long existingUserId = 1L;
        String password = "password";

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        String firstName = "Ervin";
        String lastName = "Howell";
        String email = "ervinh94@yahoo.com";

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,email,password, authorities);

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted comment");
        given(commentService.deleteComment(postId, existingCommentId, currentUser)).willReturn(apiResponse);


        /*
        //when ... then
        mvc.perform(delete("/api/posts/{postId}/comments/{id}",postId, existingCommentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isOk());
        */

    }

    @Test
    public void givenNonExistingComment_whenDeleteComment_thenIsNotFound() throws Exception {
        //given
        Long postId = 1L;
        Long nonExistingCommentId = 8L;


        String existingUsername = "ervin";
        Long existingUserId = 1L;
        String password = "password";

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        String firstName = "Ervin";
        String lastName = "Howell";
        String email = "ervinh94@yahoo.com";

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,email,password, authorities);

        given(commentService.deleteComment(postId, nonExistingCommentId, currentUser)).willThrow(new ResourceNotFoundException(COMMENT_STR, ID_STR, nonExistingCommentId));

        /*
        //when ... then
        mvc.perform(delete("/api/posts/{postId}/comments/{id}",postId, nonExistingCommentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isNotFound());
        */

    }

    @Test
    public void givenExistingComment_whenDeleteComment_thenIsUnauthorized() throws Exception {
        //given
        Long postId = 1L;
        Long nonExistingCommentId = 8L;


        String existingUsername = "ervin";
        Long existingUserId = 1L;
        String password = "password";

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        String firstName = "Ervin";
        String lastName = "Howell";
        String email = "ervinh94@yahoo.com";

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,email,password, authorities);

        given(commentService.deleteComment(postId, nonExistingCommentId, currentUser)).willThrow(new BlogapiException(HttpStatus.UNAUTHORIZED, YOU_DON_T_HAVE_PERMISSION_TO + "delete" + THIS_COMMENT));

        /*
        //when ... then
        mvc.perform(delete("/api/posts/{postId}/comments/{id}",postId, nonExistingCommentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isUnauthorized());
        */
    }


}
