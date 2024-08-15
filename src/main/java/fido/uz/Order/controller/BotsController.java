package fido.uz.Order.controller;

import fido.uz.Order.dto.BotDto;
import fido.uz.Order.dto.ResponseBot;
import fido.uz.Order.service.BotsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/api")
@RestController
public class BotsController {
    private BotsService botService;

    public BotsController(BotsService botService) {
        this.botService = botService;
    }
    @PostMapping("/create-bot")
    public ResponseEntity<?> createBot(BotDto botDto){
        ResponseBot bot = botService.createBot(botDto);
        return ResponseEntity.ok(bot);
    }
}
