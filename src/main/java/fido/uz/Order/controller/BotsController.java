package fido.uz.Order.controller;

import fido.uz.Order.dto.BotDto;
import fido.uz.Order.dto.ResponseBot;
import fido.uz.Order.service.BotsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/bots")
@RestController
public class BotsController {

    private final BotsService botService;

    @Autowired
    public BotsController(BotsService botService) {
        this.botService = botService;
    }

    @Operation(summary = "Create a new bot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bot successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class))),
            @ApiResponse(responseCode = "409", description = "Bot with this token already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseBot> createBot(@RequestBody BotDto botDto) {
        ResponseBot botResponse = botService.createBot(botDto);
        if (botResponse.getText().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(botResponse);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(botResponse);
    }

    @Operation(summary = "Retrieve all bots for a given user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bots successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class))),
            @ApiResponse(responseCode = "404", description = "No bots found for this user",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class)))
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseBot> getAll(@RequestParam Long userId) {
        ResponseBot results = botService.getAll(userId);
        if (results.getText().contains("No bots found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(results);
        }
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Retrieve a bot by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class))),
            @ApiResponse(responseCode = "404", description = "Bot not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseBot> getBotById(@PathVariable Long id) {
        ResponseBot botResponse = botService.getBotById(id);
        if (botResponse.getText().contains("Bot not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(botResponse);
        }
        return ResponseEntity.ok(botResponse);
    }

    @Operation(summary = "Update an existing bot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot successfully updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class))),
            @ApiResponse(responseCode = "404", description = "Bot not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseBot> updateBot(@PathVariable Long id, @RequestBody BotDto botDto) {
        ResponseBot botResponse = botService.updateBot(id, botDto);
        if (botResponse.getText().contains("Bot not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(botResponse);
        }
        return ResponseEntity.ok(botResponse);
    }

    @Operation(summary = "Delete a bot by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bot successfully deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class))),
            @ApiResponse(responseCode = "404", description = "Bot not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseBot.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBot> deleteBot(@PathVariable Long id) {
        ResponseBot botResponse = botService.deleteBot(id);
        if (botResponse.getText().contains("Bot not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(botResponse);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(botResponse);
    }
}
