package com.bpm.example.demo4.calendar;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.calendar.BusinessCalendarImpl;
import org.flowable.common.engine.impl.runtime.ClockReader;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import java.util.Date;

public class CustomBusinessCalendar extends BusinessCalendarImpl {
    public CustomBusinessCalendar(ClockReader clockReader) {
        super(clockReader);
    }

    @Override
    public Date resolveDuedate(String duedate, int maxIterations) {
        try {
            if (duedate.startsWith("P")) {
                //获取当前时间
                LocalDateTime currentTime = LocalDateTime.now();
                //currentTime = new LocalDateTime(2023, 9, 4, 22, 21, 19, 0);;
                LocalDateTime startTime = null;
                LocalDateTime endTime = null;
                if (currentTime.getDayOfWeek() == 6) {
                    startTime = getLocalDateTime(currentTime.plusDays(2), 9, 0, 0);
                    endTime = getLocalDateTime(currentTime.plusDays(2), 18, 0, 0);
                } else if (currentTime.getDayOfWeek() == 7) {
                    startTime = getLocalDateTime(currentTime.plusDays(1), 9, 0, 0);
                    endTime = getLocalDateTime(currentTime.plusDays(1), 18, 0, 0);
                } else {
                    startTime = getLocalDateTime(currentTime, 9, 0, 0);
                    endTime = getLocalDateTime(currentTime, 18, 0, 0);
                }
                LocalDateTime effectiveStartTime = null;
                if (currentTime.toDate().before(startTime.toDate())) {
                    effectiveStartTime = startTime;
                } else if (currentTime.toDate().after(startTime.toDate())
                        && currentTime.toDate().before(endTime.toDate())) {
                    effectiveStartTime = currentTime;
                } else if (currentTime.toDate().after(endTime.toDate())) {
                    effectiveStartTime = startTime.plusDays(1);
                    while (effectiveStartTime.getDayOfWeek() == 6
                            || effectiveStartTime.getDayOfWeek() == 7) {
                        effectiveStartTime = effectiveStartTime.plusDays(1);
                    }

                }
                Duration totalDuration
                        = new Duration(effectiveStartTime.toDateTime(),
                        (getLocalDateTime(effectiveStartTime, 18, 0, 0)).toDateTime());
                Duration duedateDuration = Period.parse(duedate).toStandardDuration();
                if (totalDuration.isLongerThan(duedateDuration)) {
                    return effectiveStartTime.plus(Period.parse(duedate)).toDate();
                } else {
                    LocalDateTime nextDay = effectiveStartTime;
                    while(true) {
                        nextDay = nextDay.plusDays(1);
                        if (nextDay.getDayOfWeek() == 6 || nextDay.getDayOfWeek() == 7) {
                            continue;
                        }
                        Duration nextDayDuration = new Duration(
                                getLocalDateTime(nextDay, 9, 0, 0).toDateTime(),
                                (getLocalDateTime(nextDay, 18, 0,0)).toDateTime());
                        if (totalDuration.plus(nextDayDuration)
                                .isShorterThan(duedateDuration)) {
                            totalDuration = totalDuration.plus(nextDayDuration);
                        } else {
                            return getLocalDateTime(nextDay, 9, 0, 0).plus(
                                    duedateDuration.minus(totalDuration)).toDate();
                        }
                    }
                }
            }
            return DateTime.parse(duedate).toDate();
        } catch (Exception e) {
            throw new FlowableException("couldn't resolve duedate: " + e.getMessage(), e);
        }
    }

    private LocalDateTime getLocalDateTime(LocalDateTime dateTime, int hourOfDay,
               int minuteOfHour, int secondOfMinute) {
        return new LocalDateTime(dateTime.getYear(), dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(), hourOfDay, minuteOfHour, secondOfMinute);
    }
}