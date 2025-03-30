package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.domain.Role;
import com.nickolas.mktbackend.exception.UserException;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = jwtProvider.getEmailFromToken(jwt);


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Користувача не існує з email: " + email));

        return user;
    }

    public User getByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("Користувача не знайдено"));

    }

    public User changeUserRole(Long userId, Role newRole) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("Користувача не знайдено"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    public boolean hasRole(String email, Role role) throws UserException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Користувача не існує з email: " + email));
        return user.getRole().equals(role);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long userId) throws UserException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        userRepository.delete(user);
    }

    /**
     * Отримання користувача за іменем користувача
     * @return користувач
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}
