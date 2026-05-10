package com.app.ecom.service;

import com.app.ecom.dto.AddressDto;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.model.Address;
import com.app.ecom.model.UserRole;
import com.app.ecom.model.User;
import com.app.ecom.respository.UserRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRespository userRespository;

    public List<UserResponse> fetchAllUsers() {
        List<User> userList = userRespository.findAll();
        return userList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRespository.findById(id)
                .map(this::mapToResponse);
    }

    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        updateUserFromRequest(user, userRequest);
        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }

        User savedUser = userRespository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponse updateUser(Long id, UserRequest reqUser) {
        User existingUser = userRespository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        updateUserFromRequest(existingUser, reqUser);

        User savedUser = userRespository.save(existingUser);

        return mapToResponse(savedUser);
    }

    public UserResponse changeRole(Long id) {
        User savedUser = userRespository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (savedUser.getRole().equals(UserRole.CUSTOMER)) {
            savedUser.setRole(UserRole.ADMIN);
        }  else if(savedUser.getRole().equals(UserRole.ADMIN)) {
            savedUser.setRole(UserRole.CUSTOMER);
        }
        savedUser = userRespository.save(savedUser);
        return mapToResponse(savedUser);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if ( user.getAddress() != null ) {
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setCountry(user.getAddress().getCountry());
            addressDto.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDto);
        }

        return response;
    }

    private void updateUserFromRequest(User user, UserRequest request) {
        if(request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getEmail() != null) user.setEmail(request.getEmail());
        if(request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            Address address = user.getAddress();

            if (address == null) {
                address = new Address();
            }

            if (request.getAddress().getStreet() != null)
                address.setStreet(request.getAddress().getStreet());

            if (request.getAddress().getCity() != null)
                address.setCity(request.getAddress().getCity());

            if (request.getAddress().getCountry() != null)
                address.setCountry(request.getAddress().getCountry());

            if (request.getAddress().getZipcode() != null)
                address.setZipcode(request.getAddress().getZipcode());

            user.setAddress(address);
        }
    }
}
