package fido.uz.Order.service;


import fido.uz.Order.entity.User;
import fido.uz.Order.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {

        Iterable<User> all = userRepository.findAll();

        return (List<User>) all;
    }
}