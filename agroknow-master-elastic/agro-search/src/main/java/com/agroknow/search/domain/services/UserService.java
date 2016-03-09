package com.agroknow.search.domain.services;

import com.agroknow.search.domain.entities.User;
import com.agroknow.search.domain.repositories.UserRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author aggelos
 */
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public User findByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public User findByToken(String token) {
        return userRepository.findByToken(token);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User register(User user) {
        Md5PasswordEncoder md5encoder = new Md5PasswordEncoder();
        String token = md5encoder.encodePassword(user.getEmail(), "agroapi" + (new Date().getTime()));

        user.setToken(token);
        user = this.save(user);
        emailService.sendRegistrationMail(user.getEmail(), token);

        return user;
    }
}
