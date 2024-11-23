package me.t65.rssfeedsourcetask.rss;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.NoArgGenerator;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RssIdGeneratorImpl implements RssIdGenerator {

    private final NoArgGenerator uuidGenerator;

    public RssIdGeneratorImpl() {
        // Generate V7 UUID (Lower risk of collision and no security issues as with v1)
        uuidGenerator = Generators.timeBasedEpochGenerator();
    }

    @Override
    public UUID generateId() {
        return uuidGenerator.generate();
    }
}
