package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.exception.UserException;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new UserException("User not exist with email: " + email));

        return user;
    }

    public User getByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}
