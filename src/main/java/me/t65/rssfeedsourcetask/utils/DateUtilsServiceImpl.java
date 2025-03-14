package me.t65.rssfeedsourcetask.utils;

import me.t65.rssfeedsourcetask.emitter.DBEmitter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class DateUtilsServiceImpl implements DateUtilsService {

    private final DBEmitter DBEmitter;

    DateUtilsServiceImpl(DBEmitter DBEmitter) {
        this.DBEmitter = DBEmitter;
    }
    @Override
    public Date getCurrentDate() {
        return new Date(Instant.now().toEpochMilli());
    }

    @Override
    public Date transformStringToDate(String date) {
        Instant instant = Instant.parse(date);
        return Date.from(instant);
        
    }

}
