package org.openpaas.paasta.marketplace.api.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public final class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String DEFAULT_TIME = "T00:00:00";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    public static final LocalDateTimeConverter instance = new LocalDateTimeConverter();

    @Override
    public LocalDateTime convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        String text = source;
        if (source != null && text.length() == 10) {
            text += DEFAULT_TIME;
        }

        LocalDateTime dateTime = LocalDateTime.parse(text, FORMATTER);

        return dateTime;
    }

}
