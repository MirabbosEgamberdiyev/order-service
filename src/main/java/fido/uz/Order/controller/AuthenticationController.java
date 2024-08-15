package fido.uz.Order.controller;

import fido.uz.Order.service.AuthenticationService;
import fido.uz.Order.dto.ChangePasswordDto;
import fido.uz.Order.dto.LoginUserDto;
import fido.uz.Order.dto.RegisterUserDto;
import fido.uz.Order.dto.ResetPasswordDto;
import fido.uz.Order.entity.User;
import fido.uz.Order.jwt.JwtService;
import fido.uz.Order.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup-user")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpUser(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signup-admin")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> registerAdmin(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpAdmin(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signup-super-admin")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<User> registerSuperAdmin(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpSuperAdmin(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext(); // Clear security context to log out user
        return ResponseEntity.ok("Logged out successfully");
    }

    @DeleteMapping("/delete-account")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteAccount() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        User user = authenticationService.getUserByEmail(email);
        if (user == null){
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST+ " Bunday foydalanufchi mavjud emas ");
        }
        authenticationService.deleteUser(email);

        return ResponseEntity.ok("Account deleted successfully");
    }

    @PostMapping("/change-password")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        changePasswordDto.setEmail(email);
        authenticationService.changePassword(changePasswordDto);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/reset-password")
//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        authenticationService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok("Password reset successfully");
    }
}
