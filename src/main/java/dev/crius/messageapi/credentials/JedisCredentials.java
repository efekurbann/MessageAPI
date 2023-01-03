package dev.crius.messageapi.credentials;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JedisCredentials {

    private final String ip;
    private final String password;
    private final int port;

}
