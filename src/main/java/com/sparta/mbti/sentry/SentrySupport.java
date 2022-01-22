package com.sparta.mbti.sentry;

import io.sentry.EventProcessor;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentrySupport implements EventProcessor {

    public SentrySupport() {
        System.out.println("===SentrySupport : init ===");
        Sentry.init("https://ce63463040a74b5e9c88b5499c256cf8@o1119561.ingest.sentry.io/6154130");
    }

    public void logSimpleMessage(String message) {
        // This sends an event to Sentry
        Message sentryMessage = new Message();
        sentryMessage.setMessage(message);

        SentryEvent event = new SentryEvent();
        event.setLevel(SentryLevel.DEBUG);
        event.setLogger(SentrySupport.class.getName());
        Sentry.captureEvent(event);
    }


}
