package jsonoperations.serialization;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class LocalDateTimeSerializer extends EasySerializer<LocalDateTime>{
    @Override
    public String serialize(LocalDateTime date) {
        return String.valueOf("\"do you remember? ".concat(String.valueOf(date.get(ChronoField.DAY_OF_WEEK))).concat("\""));
    }
}
