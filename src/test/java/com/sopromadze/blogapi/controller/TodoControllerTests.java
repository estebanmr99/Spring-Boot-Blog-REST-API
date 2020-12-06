package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TodoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopromadze.blogapi.utils.AppConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TodoController.class})
@WebMvcTest(controllers =  TodoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TodoControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TodoController todoController;

    @MockBean
    private Role role;

    @Test
    public void givenTodoId_whenDeletingToDo_thenIsOK() throws Exception {
        //given
        String existingUsername = "leanne";
        String existingFirstName = "Leanne";
        String existingLastName = "Graham";
        String existingPassword = "password";
        String existingEmailAddress = "leanne.graham@gmail.com";
        Long existingUserId = 1L;

        Long todoID = 1L;

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        UserPrincipal currentUser = new UserPrincipal(existingUserId, existingFirstName, existingLastName, existingUsername, existingEmailAddress, existingPassword, authorities);

        ApiResponse apiResponse = new ApiResponse(Boolean.TRUE, "You successfully deleted todo");


        given(todoService.deleteTodo(todoID, currentUser)).willReturn(apiResponse);

        //when ... then
        mvc.perform(delete("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isOk());
    }

    @Test
    public void givenWrongTodoId_whenDeletingToDo_thenIsResourceNotFound() throws Exception {
        //given
        Long todoID = 999L;

        given(todoController.deleteTodo(eq(todoID), isNull())).willThrow(new ResourceNotFoundException(TODO, ID, todoID));

        //when ... then
        mvc.perform(delete("/api/todos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isNotFound());
    }

}
