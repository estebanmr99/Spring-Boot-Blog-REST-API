package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.JwtAuthenticationEntryPoint;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CustomUserDetailsService;
import com.sopromadze.blogapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void givenExistingUsername_whenUpdateUser_thenSendUser() throws Exception {
        //given
        String existingUsername = "ervin";
        Long existingUserId = 1L;
        String password = "password";

        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromJWT(anyString())).thenReturn(existingUserId);
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername(existingUsername)
                        .password(password)
                        .authorities(Collections.emptyList())
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build()
        );

        String firstName = "Ervin";
        String lastName = "Howell";
        String newEmail = "ervin.howell@gmail.com";
        String currentEmail = "ervinh94@yahoo.com";

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        User newUser = new User(firstName, lastName, existingUsername, newEmail, password);
        Address address = new Address("Victor Plains", "Suite 879", "Wisokyburgh", "0566-7771", new Geo("43.9509", "34.4618"));
        Company company = new Company("Deckow-Crist", "Proactive didactic contingency", "synergize scalable supply-chains");
        newUser.setAddress(address);
        newUser.setPhone("10-692-6593 x09125");
        newUser.setWebsite("http://erwinhowell.com");
        newUser.setCompany(company);

        UserPrincipal currentUser = new UserPrincipal(existingUserId,firstName,lastName,existingUsername,currentEmail,password, authorities);

        given(userService.updateUser(newUser, existingUsername, currentUser)).willReturn(newUser);


        //when ... then
        mvc.perform(get("/api/users/{username}","ervin")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(existingUserId)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.username", is(existingUsername)))
                .andExpect(jsonPath("$.password", is(password)))
                .andExpect(jsonPath("$.email", is(newEmail)))
                .andExpect(jsonPath("$.address.street", is("Victor Plains")))
                .andExpect(jsonPath("$.address.suite", is("Suite 879")))
                .andExpect(jsonPath("$.address.city", is("Wisokyburgh")))
                .andExpect(jsonPath("$.address.zipcode", is("0566-7771")))
                .andExpect(jsonPath("$.address.geo.lat", is("43.9509")))
                .andExpect(jsonPath("$.address.geo.lng", is("34.4618")))
                .andExpect(jsonPath("$.phone", is("10-692-6593 x09125")))
                .andExpect(jsonPath("$.website", is("http://erwinhowell.com")))
                .andExpect(jsonPath("$.company.name", is("Deckow-Crist")))
                .andExpect(jsonPath("$.company.catchPhrase", is("Proactive didactic contingency")))
                .andExpect(jsonPath("$.company.bs", is("synergize scalable supply-chains")));

    }


}
