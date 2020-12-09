package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AlbumController.class)
@WebMvcTest(controllers =  AlbumController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlbumController albumController;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private PhotoService photoService;


    @Test
    public void givenValidAlbum_whenAddAlbum_thenIsCreated() throws Exception {

        Long albumId = 1L;
        String title = "Album de prueba";
        List<Photo> photos = new ArrayList<>();

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

        AlbumRequest albumRequest = new AlbumRequest();
        albumRequest.setId(albumId);
        albumRequest.setTitle(title);
        albumRequest.setUser(user);
        albumRequest.setPhoto(photos);

        Album newAlbum = new Album();
        newAlbum.setId(albumId);
        newAlbum.setTitle(title);
        newAlbum.setUser(user);
        newAlbum.setPhoto(photos);

        ResponseEntity<Album> responseEntity = new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
        given(albumService.addAlbum(albumRequest, currentUser)).willReturn(responseEntity);

        Gson gson = new Gson();
        String newAlbumJSON = gson.toJson(albumRequest);

        mvc.perform(post("/api/albums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newAlbumJSON)
                .header("Authorization", "Bearer sometoken"))
                .andExpect(status().isOk());
    }

}
