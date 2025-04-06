package com.example.demo.extend;

import com.littlenb.snowflake.support.MillisIdGeneratorFactory;
import org.flowable.common.engine.impl.cfg.IdGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SnowFlakeIdGenerator implements IdGenerator {

    private com.littlenb.snowflake.sequence.IdGenerator idGenerator;

    public SnowFlakeIdGenerator() {
        LocalDateTime startTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0);
        long startMillis = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        MillisIdGeneratorFactory millisIdGeneratorFactory = new
                MillisIdGeneratorFactory(startMillis);
        idGenerator = millisIdGeneratorFactory.create(getMachineId());
    }

    private int getMachineId() {
        long result = 0;
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            ip = ip.replace(".", "");
            result = Long.parseLong(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return (int) result % 1024;
    }

    @Override
    public String getNextId() {
        return String.valueOf(idGenerator.nextId());
    }
}


