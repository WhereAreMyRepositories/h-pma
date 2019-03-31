package com.mateuszbuczek.pm.web;

import com.mateuszbuczek.pm.domain.User;
import com.mateuszbuczek.pm.payload.JWTLoginSuccessResponse;
import com.mateuszbuczek.pm.payload.LoginRequest;
import com.mateuszbuczek.pm.security.JwtTokenProvider;
import com.mateuszbuczek.pm.services.UserService;
import com.mateuszbuczek.pm.services.ValidationService;
import com.mateuszbuczek.pm.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.mateuszbuczek.pm.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/users")
public class UserContoller {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserService userService;

    @Autowired
    UserValidator userValidator;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        ResponseEntity<?> errorMap = validationService.validate(result);
        if(errorMap != null) {
            return errorMap;
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = TOKEN_PREFIX +  tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        // validate passwords match and password > 4
        userValidator.validate(user, result);
        ResponseEntity<?> errorMap = validationService.validate(result);
        if(errorMap != null) return errorMap;

        User newUser = userService.saveUser(user);

        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
    }
}
