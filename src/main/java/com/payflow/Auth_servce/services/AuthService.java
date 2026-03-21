package com.payflow.Auth_servce.services;

import com.payflow.Auth_servce.dtos.UserDTO;
import com.payflow.Auth_servce.exceptions.DuplicateUserException;
import com.payflow.Auth_servce.exceptions.RoleNotFoundEx;
import com.payflow.Auth_servce.exceptions.WalletCreationEx;
import com.payflow.Auth_servce.models.RoleType;
import com.payflow.Auth_servce.models.User;
import com.payflow.Auth_servce.models.UserRole;
import com.payflow.Auth_servce.repos.RoleRepository;
import com.payflow.Auth_servce.repos.UserRepository;
import com.payflow.Auth_servce.security.UserPrincipal;
import com.payflow.Auth_servce.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final WalletClient walletClient;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, WalletClient walletClient) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.walletClient = walletClient;
    }

    public UserDTO register(RegistrationRequest registrationRequest) {

        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new DuplicateUserException("Email already exists");
        }

        User user = new User();

        user.setEmail(registrationRequest.email());
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setFirstName(registrationRequest.firstName());
        user.setLastName(registrationRequest.lastName());
        user.setCurrency(registrationRequest.currency().toUpperCase());
        user.setCredentialsExpired(false);
        user.setLocked(false);
        user.setEnabled(true);

        UserRole role = roleRepository.findByRoleName(RoleType.USER).orElseThrow(() -> new RoleNotFoundEx("role not found."));
        user.setRole(role);

        User createdUser = userRepository.save(user);

        try {
            WalletRequest walletRequest = new WalletRequest(createdUser.getUserId(), registrationRequest.currency());
            logger.info("userId={}, currency={}", createdUser.getUserId(), registrationRequest.currency());
            walletClient.createWallet(walletRequest);

            return HelperUtility.convertToUserDTO(createdUser);

        } catch (Exception e) {
            userRepository.delete(createdUser);
            throw new WalletCreationEx("User created but failed to create wallet for user.");
        }

    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        if (authentication.isAuthenticated()) {

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userDetails);

            return new LoginResponse(accessToken);
        }

        throw new UsernameNotFoundException("Invalid username or password.");
    }

    public UserDTO getMe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return HelperUtility.convertToUserDTO(user);
    }

}
