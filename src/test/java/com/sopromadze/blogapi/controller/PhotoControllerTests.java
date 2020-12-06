package com.sopromadze.blogapi.controller;

import com.google.gson.Gson;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.security.JwtTokenProvider;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PhotoService;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static com.sopromadze.blogapi.utils.AppConstants.ALBUM;
import static com.sopromadze.blogapi.utils.AppConstants.ID;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PhotoController.class})
@WebMvcTest(controllers =  PhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PhotoControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private PhotoService photoService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AlbumRepository albumRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private Role role;

    @Test
    public void givenPhotoInformation_whenAddingPhoto_thenIsOK() throws Exception {
        //given
        String existingUsername = "leanne";
        String existingFirstName = "Leanne";
        String existingLastName = "Graham";
        String existingPassword = "password";
        String existingEmailAddress = "leanne.graham@gmail.com";
        Long existingUserId = 1L;

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        UserPrincipal currentUser = new UserPrincipal(existingUserId, existingFirstName, existingLastName, existingUsername, existingEmailAddress, existingPassword, authorities);

        String photoTitle = "Photo test";
        String photoURL= "https://via.placeholder.com/600/92c952";
        String photoThumbnailURL = "https://via.placeholder.com/150/92c952";
        Long albumID = 1L;
        Long photoID = 1L;

        PhotoResponse photoResponse = new PhotoResponse(photoID, photoTitle, photoURL, photoThumbnailURL, albumID);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle(photoTitle);
        photoRequest.setUrl(photoURL);
        photoRequest.setThumbnailUrl(photoThumbnailURL);
        photoRequest.setAlbumId(albumID);

        given(photoService.addPhoto(photoRequest, currentUser)).willReturn(photoResponse);

        Gson gson = new Gson();
        String newPhotoRequest= gson.toJson(photoRequest);

        //when ... then
        mvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPhotoRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void givenWrongPhotoInformation_whenAddingPhoto_thenIsBadRequest() throws Exception {
        //given
        String existingUsername = "leanne";
        String existingFirstName = "Leanne";
        String existingLastName = "Graham";
        String existingPassword = "password";
        String existingEmailAddress = "leanne.graham@gmail.com";
        Long existingUserId = 1L;

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        UserPrincipal currentUser = new UserPrincipal(existingUserId, existingFirstName, existingLastName, existingUsername, existingEmailAddress, existingPassword, authorities);

        String photoTitle = "Photo test";
        String photoURL= "https://via.placeholder.com/600/92c952";
        String photoThumbnailURL = "https://via.placeholder.com/150/92c952";
        Long albumID = null;
        Long photoID = 1L;

        PhotoResponse photoResponse = new PhotoResponse(photoID, photoTitle, photoURL, photoThumbnailURL, albumID);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle(photoTitle);
        photoRequest.setUrl(photoURL);
        photoRequest.setThumbnailUrl(photoThumbnailURL);
        photoRequest.setAlbumId(albumID);

        given(albumRepository.findById(photoRequest.getAlbumId())).willReturn(Optional.empty());
        given(photoService.addPhoto(photoRequest, currentUser)).willReturn(photoResponse);

        Gson gson = new Gson();
        String newPhotoRequest= gson.toJson(photoRequest);

        //when ... then
        mvc.perform(post("/api/photos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPhotoRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingPhotoInformation_whenUpdatingPhoto_thenIsOK() throws Exception {
        //given
        String existingUsername = "leanne";
        String existingFirstName = "Leanne";
        String existingLastName = "Graham";
        String existingPassword = "password";
        String existingEmailAddress = "leanne.graham@gmail.com";
        Long existingUserId = 1L;

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        UserPrincipal currentUser = new UserPrincipal(existingUserId, existingFirstName, existingLastName, existingUsername, existingEmailAddress, existingPassword, authorities);

        String photoTitle = "New photo test";
        String photoURL= "https://via.placeholder.com/600/anotherTest";
        String photoThumbnailURL = "https://via.placeholder.com/150/test";
        Long albumID = 1L;
        Long photoID = 1L;

        PhotoResponse photoResponse = new PhotoResponse(photoID, photoTitle, photoURL, photoThumbnailURL, albumID);

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle(photoTitle);
        photoRequest.setUrl(photoURL);
        photoRequest.setThumbnailUrl(photoThumbnailURL);
        photoRequest.setAlbumId(albumID);

        given(photoService.updatePhoto(photoID, photoRequest,currentUser)).willReturn(photoResponse);

        Gson gson = new Gson();
        String newPhotoRequest= gson.toJson(photoRequest);

        //when ... then
        mvc.perform(put("/api/photos/{id}", photoID)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPhotoRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void givenWrongExistingPhotoInformation_whenUpdatingPhoto_thenIsBadRequest() throws Exception {
        //given
        String existingUsername = "leanne";
        String existingFirstName = "Leanne";
        String existingLastName = "Graham";
        String existingPassword = "password";
        String existingEmailAddress = "leanne.graham@gmail.com";
        Long existingUserId = 1L;

        Role role1 = new Role(RoleName.ROLE_USER);
        List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles = Collections.unmodifiableList(roles);
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

        UserPrincipal currentUser = new UserPrincipal(existingUserId, existingFirstName, existingLastName, existingUsername, existingEmailAddress, existingPassword, authorities);

        String photoTitle = "New photo test";
        String photoURL= "https://via.placeholder.com/600/anotherTest";
        String photoThumbnailURL = "https://via.placeholder.com/150/test";
        Long albumID = null;
        Long photoID = null;

        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setTitle(photoTitle);
        photoRequest.setUrl(photoURL);
        photoRequest.setThumbnailUrl(photoThumbnailURL);
        photoRequest.setAlbumId(albumID);

        given(photoService.updatePhoto(photoID, photoRequest,currentUser)).willReturn(null);

        Gson gson = new Gson();
        String newPhotoRequest= gson.toJson(photoRequest);

        //when ... then
        mvc.perform(put("/api/photos/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer sometoken")
                .content(newPhotoRequest))
                .andExpect(status().isBadRequest());
    }
}
