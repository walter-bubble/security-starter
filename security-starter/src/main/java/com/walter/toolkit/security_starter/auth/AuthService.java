package com.walter.toolkit.security_starter.auth;

import com.walter.toolkit.security_starter.dto.AuthResponse;
import com.walter.toolkit.security_starter.dto.LoginRequest;
import com.walter.toolkit.security_starter.exception.ApiException;
import com.walter.toolkit.security_starter.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthService(AuthenticationManager authenticationManager,JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    public AuthResponse login(LoginRequest loginRequest){
        Objects.requireNonNull(loginRequest,"Login must not be null");
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.emailAddress(),loginRequest.password()));
            if(!(authentication.getPrincipal() instanceof UserDetails userDetails)){
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"ERR_AUTH_PRINCIPAL","Unexpected authentication principal type");
            }
            List<String> authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String token = jwtService.generateAccessToken(userDetails.getUsername(), authorities);
            return new AuthResponse(token);
        }catch (BadCredentialsException exception){
            throw new ApiException(HttpStatus.UNAUTHORIZED,"ERR_BAD_CREDENTIALS","Invalid EmailAddress or Password");
        }catch(DisabledException | LockedException exception){
            throw new ApiException(HttpStatus.FORBIDDEN,"ERR_ACCOUNT_RESTRICTED","User account has been disabled or locked");
        }
    }
}
