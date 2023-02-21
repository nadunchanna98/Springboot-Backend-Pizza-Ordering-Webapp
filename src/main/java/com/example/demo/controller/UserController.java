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
    User newUser(@RequestBody User newUser) throws Exception {
        String encryptedPassword = AESEncryption.encrypt(newUser.getPassword());
        newUser.setPassword(encryptedPassword);
        return userRepository.save(newUser);
    }

    @GetMapping("/users")
    List<User>  getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        String decryptedPassword = AESEncryption.decrypt(user.getPassword());
        user.setPassword(decryptedPassword);
        return user;
    }

   @GetMapping("/user/email/{email}")
   Object checkEmailExists(@PathVariable String email){
        return userRepository.findByEmail(email);
   }

    @PutMapping("/user/change/{id}")
    User updateUser(@RequestBody User newUser, @PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    if (newUser.getUsername() != null) {
                        user.setUsername(newUser.getUsername());
                    }
                    if (newUser.getEmail() != null) {
                        user.setEmail(newUser.getEmail());
                    }
                    if (newUser.getAddress() != null) {
                        user.setAddress(newUser.getAddress());
                    }
                    if (newUser.getPhone() != null) {
                        user.setPhone(newUser.getPhone());
                    }
                    if (newUser.getName() != null) {
                        user.setName(newUser.getName());
                    }
                    if (newUser.getTitle() != null) {
                        user.setTitle(newUser.getTitle());
                    }
                    if (newUser.getPassword() != null) {
                        String hashedPassword = PasswordEncoder.encode(newUser.getPassword());
                        user.setPassword(hashedPassword);
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        return "User with id "+id+" has been deleted success. ";
    }

    @PostMapping("/user/login")
    public String login(@RequestBody User request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        // retrieve the user from the database by username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "Invalid username or password";
        }
        String encryptedPassword = user.getPassword();
        String decryptedPassword = AESEncryption.decrypt(encryptedPassword);
        if (password.equals(decryptedPassword)) {
            return "Login successful";
        } else {
            return "Invalid username or password";
        }
    }
    

        }
