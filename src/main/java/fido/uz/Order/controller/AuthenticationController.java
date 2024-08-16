package fido.uz.Order.controller;

import fido.uz.Order.service.AuthenticationService;
import fido.uz.Order.dto.ChangePasswordDto;
import fido.uz.Order.dto.LoginUserDto;
import fido.uz.Order.dto.RegisterUserDto;
import fido.uz.Order.dto.ResetPasswordDto;
import fido.uz.Order.entity.User;
import fido.uz.Order.jwt.JwtService;
import fido.uz.Order.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signup-user")
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpUser(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Register a new admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signup-admin")
    public ResponseEntity<User> registerAdmin(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpAdmin(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Register a new super admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Super admin successfully registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signup-super-admin")
    public ResponseEntity<User> registerSuperAdmin(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signUpSuperAdmin(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Authenticate user and generate JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated and token generated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Logout the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged out",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Logout failed",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(summary = "Delete the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account successfully deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "User not found or other error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        User user = authenticationService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        authenticationService.deleteUser(email);
        return ResponseEntity.ok("Account deleted successfully");
    }

    @Operation(summary = "Change the current user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid password or other error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        changePasswordDto.setEmail(email);
        authenticationService.changePassword(changePasswordDto);

        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Reset a user's password (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        authenticationService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok("Password reset successfully");
    }
}
