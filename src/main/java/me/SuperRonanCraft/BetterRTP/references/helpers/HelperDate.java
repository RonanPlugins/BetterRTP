package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.Settings;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HelperDate {

    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    public static String left(Long amount) {
        Date current_date = HelperDate.getDate();
        return fromTo(current_date.getTime(), amount);
    }
    public static String total(Long amount) {
        return fromTo(0L, amount);
    }

    public static String fromTo(Long from, Long to) {
        Settings settings = BetterRTP.getInstance().getSettings();
        long min = Math.min(from, to);
        long max = Math.max(from, to);
        if (max == min)
            return settings.getPlaceholder_timeZero();
        if (max < 0L || min < 0L)
            return settings.getPlaceholder_timeInf();
        long diffInMillies = max - min;
        long days, hours, minutes, seconds;

        days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= (((1000 * 60) * 60) * 24) * days;
        hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= ((1000 * 60) * 60) * hours;
        minutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= (1000 * 60) * minutes;
        seconds = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        String time_str = "";
        if (days > 0)
            time_str += settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(days));
        if (days > 0 || hours > 0)
            time_str += settings.getPlaceholder_timeHours().replace("{0}", String.valueOf(hours));
        if (days > 0 || hours > 0 || minutes > 0)
            time_str += settings.getPlaceholder_timeMinutes().replace("{0}", String.valueOf(minutes));
        time_str += settings.getPlaceholder_timeSeconds().replace("{0}", String.valueOf(seconds));

        return time_str;
    }
}
