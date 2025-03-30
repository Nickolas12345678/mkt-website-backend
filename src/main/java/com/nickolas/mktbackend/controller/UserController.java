package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.exception.UserException;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.request.RoleChangeRequest;
import com.nickolas.mktbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfileHandler(
            @RequestHeader("Authorization") String jwt) throws UserException {

        System.out.println("/api/users/profile");
        User user = userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) throws UserException {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role/{userId}")
    public ResponseEntity<User> changeUserRole(
            @RequestHeader("Authorization") String jwt,
            @PathVariable("userId") Long userId,
            @RequestBody RoleChangeRequest roleChangeRequest) throws UserException {


        User updatedUser = userService.changeUserRole(userId, roleChangeRequest.getRole());


        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
            @RequestHeader("Authorization") String jwt,
            @PathVariable("userId") Long userId) throws UserException {

        userService.deleteUserById(userId);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
