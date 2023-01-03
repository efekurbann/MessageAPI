package dev.crius.messageapi.sponge.logger;

import dev.crius.messageapi.logger.Logger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpongeLogger implements Logger {

    private final org.apache.logging.log4j.Logger logger;

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
