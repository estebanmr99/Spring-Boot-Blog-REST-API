package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.config.SecutiryConfig;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.CustomUserDetailsService;
import com.sopromadze.blogapi.service.PostService;
import com.sopromadze.blogapi.service.UserService;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserController.class)
@WebMvcTest(controllers =  UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecutiryConfig secutiryConfig;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    public void givenExistingUsername_whenUpdateUser_thenIsCreated() throws Exception {
        //given
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

        User newUser = new User(firstName, lastName, existingUsername, newEmail, password);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        newUser.setAddress(address);
        newUser.setPhone("10-692-6593 x09125");
        newUser.setWebsite("http://erwinhowell.com");
        newUser.setCompany(company);

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,currentEmail,password, authorities);

        given(userService.updateUser(newUser, existingUsername, currentUser)).willReturn(newUser);

        Gson gson = new Gson();
        String newUserJSON = gson.toJson(newUser);

        //when ... then
        mvc.perform(put("/api/users/{username}",existingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newUserJSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenUserWithoutLastName_whenUpdateUser_thenSendBadRequest() throws Exception {
        //given
        String existingUsername = "ervin";
        String password = "password";

        String firstName = "Ervin";
        String newEmail = "ervin.howell@gmail.com";

        User newUser = new User(firstName, null, existingUsername, newEmail, password);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        newUser.setAddress(address);
        newUser.setPhone("10-692-6593 x09125");
        newUser.setWebsite("http://erwinhowell.com");
        newUser.setCompany(company);

        Gson gson = new Gson();
        String newUserJSON = gson.toJson(newUser);

        //when ... then
        mvc.perform(put("/api/users/{username}",existingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newUserJSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserWithoutPassword_whenUpdateUser_thenSendBadRequest() throws Exception {
        //given
        String existingUsername = "ervin";

        String firstName = "Ervin";
        String lastName = "Howell";
        String newEmail = "ervin.howell@gmail.com";

        User newUser = new User(firstName, lastName, existingUsername, newEmail, null);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        newUser.setAddress(address);
        newUser.setPhone("10-692-6593 x09125");
        newUser.setWebsite("http://erwinhowell.com");
        newUser.setCompany(company);

        Gson gson = new Gson();
        String newUserJSON = gson.toJson(newUser);

        //when ... then
        mvc.perform(put("/api/users/{username}",existingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newUserJSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingUsername_whenGetUserProfile_thenIsOk() throws Exception {
        //given
        String existingUsername = "ervin";
        Long existingUserId = 1L;
        String password = "password";

        String firstName = "Ervin";
        String lastName = "Howell";
        String email = "ervinh94@yahoo.com";

        UserProfile userProfile = new UserProfile();
        userProfile.setId(existingUserId);
        userProfile.setUsername(existingUsername);
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        userProfile.setJoinedAt(Instant.now());
        userProfile.setEmail(email);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        userProfile.setAddress(address);
        userProfile.setPhone("10-692-6593 x09125");
        userProfile.setWebsite("http://erwinhowell.com");
        userProfile.setCompany(company);

        given(userService.getUserProfile(existingUsername)).willReturn(userProfile);

        //when ... then
        mvc.perform(get("/api/users/{username}/profile",existingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNonExistingUsername_whenGetUserProfile_thenIsNotFound() throws Exception {
        //given
        String nonExistingUsername = "usuario2";

        given(userService.getUserProfile(nonExistingUsername)).willThrow(new ResourceNotFoundException("User", "username", nonExistingUsername));

        //when ... then
        mvc.perform(get("/api/users/{username}/profile",nonExistingUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isNotFound());
    }
}