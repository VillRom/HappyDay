package ru.romanchev.happyday.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {

    @Value(value = "${bot.name}")
    private String botName;

    @Value(value = "${bot.key}")
    private String botKey;

    @Value(value = "${admin.bot.id}")
    private Long adminId;
}
