package com.offline.BookStoreManagement.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.offline.BookStoreManagement.exceptions.DatabaseException;
import com.offline.BookStoreManagement.exceptions.InvalidInputException;
import com.offline.BookStoreManagement.exceptions.ResourceNotFoundException;
import com.offline.BookStoreManagement.model.Users;
import com.offline.BookStoreManagement.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users registerUser(Users user) {
        try {
            return userRepository.save(user);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to register user: " + e.getMessage());
        }
    }

    public Users getUserByUsername(String username) {
        Users u=userRepository.findByUsername(username);
        if(u==null){
        	throw new ResourceNotFoundException("User not found with username: " + username);  	
        }
        return u;
    }

    public Users updateUser(Long id, Users user) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        try {
            return userRepository.save(existingUser);
        } catch (ConstraintViolationException e) {
            throw new InvalidInputException("Validation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("Failed to update user: " + e.getMessage());
        }
    }

	public Optional<Users> findByEmail(String email) {
		// TODO Auto-generated method stub
		Optional<Users> u= Optional.of(userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)));
		return u;
	}
	
	public Optional<Users> findByUserId(Long userId) {
		// TODO Auto-generated method stub
		Optional<Users> u= Optional.of(userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userId)));
		return u;
	}
}


