package com.example.demo.controller;

import com.example.demo.exception.EmailNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    User newUser(@RequestBody User newUser){
        return userRepository.save(newUser);
    }

    @GetMapping("/users")
    List<User>  getAllUsers(){
        return userRepository.findAll();
    }

   @GetMapping("/users/user/{id}")
    User getUserById(@PathVariable Long id){
        return userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException(id));
   }

   @GetMapping("/users/user/email/{email}")
   Object checkEmailExists(@PathVariable String email){
        return userRepository.findByEmail(email);
   }

}
