package fido.uz.Order.controller;

import fido.uz.Order.dto.ChangePasswordDto;
import fido.uz.Order.dto.LoginUserDto;
import fido.uz.Order.dto.RegisterUserDto;
import fido.uz.Order.dto.ResetPasswordDto;
import fido.uz.Order.entity.User;
import fido.uz.Order.jwt.JwtService;
import fido.uz.Order.response.LoginResponse;
import fido.uz.Order.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
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
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
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
//    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody RegisterUserDto registerUserDto) {
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
//    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<User> registerSuperAdmin(@Valid @RequestBody RegisterUserDto registerUserDto) {
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
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            // Log exception and return unauthorized status
            return ResponseEntity.status(401).body(null);
        }
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
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            // Log exception and return internal server error status
            return ResponseEntity.status(500).body("Logout failed");
        }
    }

    @Operation(summary = "Delete the current user's account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account successfully deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "User not found or other error",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/delete-account")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();

            User user = authenticationService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            authenticationService.deleteUser(email);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            // Log exception and return bad request status
            return ResponseEntity.badRequest().body("Failed to delete account");
        }
    }

    @Operation(summary = "Change the current user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid password or other error",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/change-password")
//    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();

            changePasswordDto.setEmail(email);
            authenticationService.changePassword(changePasswordDto);

            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            // Log exception and return bad request status
            return ResponseEntity.badRequest().body("Failed to change password");
        }
    }

    @Operation(summary = "Reset a user's password (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/reset-password")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            authenticationService.resetPassword(resetPasswordDto);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            // Log exception and return bad request status
            return ResponseEntity.badRequest().body("Failed to reset password");
        }
    }
}
