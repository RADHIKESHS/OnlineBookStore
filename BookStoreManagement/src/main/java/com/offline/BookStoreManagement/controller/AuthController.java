package com.offline.BookStoreManagement.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.offline.BookStoreManagement.config.JwtToken;
import com.offline.BookStoreManagement.config.SecurityDetails;
import com.offline.BookStoreManagement.exceptions.ResourceNotFoundException;
import com.offline.BookStoreManagement.model.LoginRequest;
import com.offline.BookStoreManagement.model.Order;
import com.offline.BookStoreManagement.model.Users;
import com.offline.BookStoreManagement.service.OrderService;
import com.offline.BookStoreManagement.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PasswordEncoder ps;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUserHandler(@RequestBody Users user) throws Exception {
        	user.setActivity("Active");
        	user.setPassword(ps.encode(user.getPassword()));
        	Users saveduser= userService.registerUser(user);
        	return new ResponseEntity<>(saveduser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUserHandler(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
        	if(!userService.findByEmail(loginRequest.getUsername()).get().getActivity().equals("Active")) {
        		throw new ResourceNotFoundException("User is Not Active");
        	}else {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
                );
                System.out.println("hi");
                String jwt = JwtToken.generateToken(loginRequest.getUsername(), authentication.getAuthorities());
                System.out.println("hi2");
                response.setHeader(SecurityDetails.JWT_HEADER, "Bearer " + jwt);
                System.out.println("hi");
                return ResponseEntity.ok("Login successful");
        	}
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<?> logOutUserHandler(HttpServletResponse response) {
        response.setHeader(SecurityDetails.JWT_HEADER, null);
        return ResponseEntity.ok("Logout successful");
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Users> getProfile() {
        String username = getCurrentUsername();
        System.out.println(username);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users userProfile = userService.findByEmail(username).get();
        return ResponseEntity.ok(userProfile);
    }
    
    @PutMapping("/updateProfile")
    public ResponseEntity<Users> updateself(@Valid @RequestBody Users user) {
    	String username = getCurrentUsername();
    	Users u= userService.findByEmail(username).get();
    	if(!u.getActivity().equals("Active")) {
    		throw new ResourceNotFoundException("Your Profile is Deactivated");
    	}else {
        	Users updatedUser = userService.updateUser(u.getId(), user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    	}

    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders() {
    	String username = getCurrentUsername();
    	Users u= userService.findByEmail(username).get();
        List<Order> orders = orderService.getUserOrders(u.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    

}

