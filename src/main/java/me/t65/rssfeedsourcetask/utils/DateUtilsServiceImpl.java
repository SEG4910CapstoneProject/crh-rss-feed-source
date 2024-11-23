package me.t65.rssfeedsourcetask.utils;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class DateUtilsServiceImpl implements DateUtilsService {
    @Override
    public Date getCurrentDate() {
        return new Date(Instant.now().toEpochMilli());
    }
}
