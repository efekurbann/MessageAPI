package dev.crius.messageapi.velocity.logger;

import dev.crius.messageapi.logger.Logger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VelocityLogger implements Logger {

    private final org.slf4j.Logger logger;

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
