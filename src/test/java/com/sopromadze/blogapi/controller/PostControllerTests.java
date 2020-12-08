package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.sopromadze.blogapi.utils.AppConstants.ID;
import static com.sopromadze.blogapi.utils.AppConstants.POST;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PostController.class)
@WebMvcTest(controllers =  PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PostService postService;

    @Test
    public void givenExistingPost_whenUpdatePost_thenIsOk() throws Exception {

        Long existingPostId = 1L;
        String newTitle = "Título de mi post actualizado";
        String newBody = " Esto es una prueba de actualización del cuerpo y título de un post.";

        Long categoryId = 3L;
        List<String> tags = new ArrayList<>(Arrays.asList("test", "new"));

        PostRequest newPostRequest = new PostRequest();
        newPostRequest.setTitle(newTitle);
        newPostRequest.setBody(newBody);
        newPostRequest.setCategoryId(categoryId);
        newPostRequest.setTags(tags);


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
        String newEmail = "ervin.howell@gmail.com";
        String currentEmail = "ervinh94@yahoo.com";

        User user = new User(firstName, lastName, existingUsername, newEmail, password);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        user.setAddress(address);
        user.setPhone("10-692-6593 x09125");
        user.setWebsite("http://erwinhowell.com");
        user.setCompany(company);

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,currentEmail,password, authorities);

        Category category = new Category("test category");
        List<Comment> comments = new ArrayList<>();
        List<Tag> postTags = new ArrayList<Tag>();
        postTags.add(new Tag("test"));
        postTags.add(new Tag("new"));
        Post newPost = new Post();
        newPost.setId(existingPostId);
        newPost.setTitle(newTitle);
        newPost.setBody(newBody);
        newPost.setUser(user);
        newPost.setCategory(category);
        newPost.setComments(comments);
        newPost.setTags(postTags);

        given(postService.updatePost(existingPostId, newPostRequest, currentUser)).willReturn(newPost);

        Gson gson = new Gson();
        String newPostRequestJSON = gson.toJson(newPostRequest);


        //when ... then
        mvc.perform(put("/api/posts/{id}",existingPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPostRequestJSON))
                .andExpect(status().isOk());

    }

    public void givingNonExistingPost_whenUpdatePost_thenIsNotFound() throws Exception {
        Long nonExistingPostId = 20L;
        String newTitle = "Título de mi post no actualizado";
        String newBody = " Esto es una prueba de actualización del cuerpo y título de un post.";

        Long categoryId = 3L;
        List<String> tags = new ArrayList<>(Arrays.asList("test", "new"));

        PostRequest newPostRequest = new PostRequest();
        newPostRequest.setTitle(newTitle);
        newPostRequest.setBody(newBody);
        newPostRequest.setCategoryId(categoryId);
        newPostRequest.setTags(tags);


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
        String currentEmail = "ervinh94@yahoo.com";

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,currentEmail,password, authorities);

        given(postService.updatePost(nonExistingPostId, newPostRequest, currentUser)).willThrow(new ResourceNotFoundException(POST, ID, nonExistingPostId));

        Gson gson = new Gson();
        String newPostRequestJSON = gson.toJson(newPostRequest);


        //when ... then
        mvc.perform(put("/api/posts/{id}",nonExistingPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPostRequestJSON))
                .andExpect(status().isNotFound());

    }

    public void givingExistingPost_whenUpdatePost_thenIsUnauthorized() throws Exception {
        Long existingPostId = 1L;
        String newTitle = "Título de mi post no actualizado";
        String newBody = " Esto es una prueba de actualización del cuerpo y título de un post.";

        Long categoryId = 3L;
        List<String> tags = new ArrayList<>(Arrays.asList("test", "new"));

        PostRequest newPostRequest = new PostRequest();
        newPostRequest.setTitle(newTitle);
        newPostRequest.setBody(newBody);
        newPostRequest.setCategoryId(categoryId);
        newPostRequest.setTags(tags);


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
        String currentEmail = "ervinh94@yahoo.com";

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,currentEmail,password, authorities);

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to edit this post");
        given(postService.updatePost(existingPostId, newPostRequest, currentUser)).willThrow(new UnauthorizedException(apiResponse));

        Gson gson = new Gson();
        String newPostRequestJSON = gson.toJson(newPostRequest);


        //when ... then
        mvc.perform(put("/api/posts/{id}",existingPostId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPostRequestJSON))
                .andExpect(status().isUnauthorized());

    }
}
