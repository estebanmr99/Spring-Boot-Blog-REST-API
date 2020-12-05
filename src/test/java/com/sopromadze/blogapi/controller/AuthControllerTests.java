package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.config.SecutiryConfig;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.LoginRequest;
import com.sopromadze.blogapi.payload.SignUpRequest;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import com.sopromadze.blogapi.service.UserService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.CoreMatchers.is;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AuthController.class})
@WebMvcTest(controllers =  AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecutiryConfig secutiryConfig;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private Role role;

    @Test
    public void givenNewCredentials_whenSignUp_thenCreateUser() throws Exception {
        //given
        String newUsername = "emenarui";
        String newFirstName = "Esteban";
        String newLastName = "Mena";
        String newPassword = "prueba@123";
        String newEmailAddress = "test@test.com";

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName(newFirstName);
        signUpRequest.setLastName(newLastName);
        signUpRequest.setUsername(newUsername);
        signUpRequest.setEmail(newEmailAddress);
        signUpRequest.setPassword(newPassword);

        Role userRole = new Role(RoleName.ROLE_USER);

        Role adminRole = new Role(RoleName.ROLE_ADMIN);

        User newUser = new User(newFirstName, newLastName, newUsername, newEmailAddress, newPassword);

        given(userRepository.existsByUsername(signUpRequest.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(false);
        given(roleRepository.findByName(RoleName.ROLE_ADMIN)).willReturn(Optional.of(adminRole));
        given(roleRepository.findByName(RoleName.ROLE_USER)).willReturn(Optional.of(userRole));
        given(userRepository.save(anyObject())).willReturn(newUser);

        Gson gson = new Gson();
        String newSignUpRequest= gson.toJson(signUpRequest);

        //when ... then
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newSignUpRequest))
                .andExpect(status().isCreated());
    }

    @Test(expected = NestedServletException.class)
    public void givenExistingCredentials_whenSignUp_thenFail() throws Exception {
        //given
        String newUsername = "leanne";
        String newFirstName = "Leanne";
        String newLastName = "Graham";
        String newPassword = "password";
        String newEmailAddress = "leanne.graham@gmail.com";

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName(newFirstName);
        signUpRequest.setLastName(newLastName);
        signUpRequest.setUsername(newUsername);
        signUpRequest.setEmail(newEmailAddress);
        signUpRequest.setPassword(newPassword);


        given(userRepository.existsByUsername(signUpRequest.getUsername())).willReturn(true);
        given(userRepository.existsByEmail(signUpRequest.getEmail())).willReturn(true);

        Gson gson = new Gson();
        String newSignUpRequest= gson.toJson(signUpRequest);

        //when ... then
        mvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newSignUpRequest))
                .andExpect(__ -> Assert.assertThat(
                        __.getResolvedException(),
                        CoreMatchers.instanceOf(NestedServletException.class)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void givenExistingCredentials_whenSignIn_thenGetToken() throws Exception {
        //given
        String existingUsername = "emenarui";
        String existingPassword = "prueba@123";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(existingUsername);
        loginRequest.setPassword(existingPassword);

        when(jwtTokenProvider.generateToken(anyObject())).thenReturn("TEST");

        Gson gson = new Gson();
        String newLoginRequest= gson.toJson(loginRequest);

        //when ... then
        mvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLoginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("TEST")));
    }


    @Test
    public void givenWrongCredentials_whenSignIn_thenNoTokenReturned() throws Exception {
        //given
        String existingUsername = "emenarui";
        String existingPassword = "prueba@123";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(existingUsername);
        loginRequest.setPassword(existingPassword);

        Gson gson = new Gson();
        String newLoginRequest = gson.toJson(loginRequest);

        //when ... then
        mvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLoginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(IsNull.nullValue()));
    }

}