package fido.uz.Order.service;

import fido.uz.Order.dto.BotDto;
import fido.uz.Order.dto.ResponseBot;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.entity.User;
import fido.uz.Order.repository.BotsRepository;
import fido.uz.Order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BotsService {

    private final BotsRepository botRepository;
    private final UserRepository userRepository;

    @Autowired
    public BotsService(BotsRepository botRepository, UserRepository userRepository) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
    }
    // Create a new bot
    public ResponseBot createBot(BotDto botDto) {
        // Check if a bot with the same token already exists
        Bot existingBot = botRepository.findByBotToken(botDto.getBot_token());
        if (existingBot != null) {
            return new ResponseBot("Bot already exists with this token.");
        }

        // Check if the user exists
        Optional<User> userOptional = userRepository.findById(botDto.getUser_id());
        if (!userOptional.isPresent()) {
            return new ResponseBot("User not found.");
        }

        // Create and save the new bot
        Bot bot = new Bot();
        bot.setBotToken(botDto.getBot_token());
        bot.setUserId(botDto.getUser_id());

        Bot savedBot = botRepository.save(bot);
        return new ResponseBot(savedBot.toString());
    }

    // Retrieve all bots for a given user ID
    public ResponseBot getAll(Long id) {
        List<Bot> bots = botRepository.findAllByUserId(id);
        if (bots.isEmpty()) {
            return new ResponseBot("No bots found for this user.");
        }
        return new ResponseBot(bots.toString());
    }

    // Retrieve a bot by its ID
    public ResponseBot getBotById(Long botId) {
        Optional<Bot> botOptional = botRepository.findById(botId);
        if (botOptional.isPresent()) {
            return new ResponseBot(botOptional.get().toString());
        } else {
            return new ResponseBot("Bot not found.");
        }
    }

    // Update an existing bot
    public ResponseBot updateBot(Long botId, BotDto botDto) {
        Optional<Bot> botOptional = botRepository.findById(botId);
        if (botOptional.isPresent()) {
            Bot bot = botOptional.get();
            bot.setBotToken(botDto.getBot_token());
            bot.setUserId(botDto.getUser_id());
            Bot updatedBot = botRepository.save(bot);
            return new ResponseBot(updatedBot.toString());
        } else {
            return new ResponseBot("Bot not found.");
        }
    }

    // Delete a bot by its ID
    public ResponseBot deleteBot(Long botId) {
        Optional<Bot> botOptional = botRepository.findById(botId);
        if (botOptional.isPresent()) {
            botRepository.deleteById(botId);
            return new ResponseBot("Bot deleted successfully.");
        } else {
            return new ResponseBot("Bot not found.");
        }
    }
}
