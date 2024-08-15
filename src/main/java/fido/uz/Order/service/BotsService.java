package fido.uz.Order.service;

import fido.uz.Order.dto.BotDto;
import fido.uz.Order.dto.ResponseBot;
import fido.uz.Order.entity.Bot;
import fido.uz.Order.repository.BotsRepository;
import org.springframework.stereotype.Service;

@Service
public class BotsService {

    private BotsRepository botRepository;

    public BotsService(BotsRepository botRepository) {
        this.botRepository = botRepository;
    }

    public ResponseBot createBot(BotDto botDto){
        Bot exist = botRepository.findByBotToken(botDto.getBot_token());
        if (exist != null){
            return new ResponseBot("Bunday bot oldin bor ");
        }
        Bot bot = new Bot();
        bot.setBotToken(botDto.getBot_token());
        bot.setUserId(botDto.getUser_id());
        Bot result = botRepository.save(bot);
        return new ResponseBot(result.toString());
    }
}
