package fido.uz.Order.repository;

import fido.uz.Order.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotsRepository extends JpaRepository<Bot, Long> {
    Bot findByBotToken(String botToken);
}
