package dev.crius.messageapi.credentials;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

@Data
@RequiredArgsConstructor
public class JedisCredentials {

    private final String ip;
    private final String password;
    private final int port;

    public JedisCredentials(ConfigurationSection section) {
        this.ip = section.getString("host");
        this.password = section.getString("password");
        this.port = section.getInt("port");
    }

}
