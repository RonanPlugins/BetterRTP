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

    public static String stringFrom(Long amount) {
        Date current_date = HelperDate.getDate();
        long min = Math.min(amount, current_date.getTime());
        long max = Math.max(amount, current_date.getTime());
        long diffInMillies = max - min;
        long days = 0, hours = 0, minutes = 0, seconds = 0;
        if (diffInMillies > 0) {
            days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            diffInMillies -= (((1000 * 60) * 60) * 24) * days;
            hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            diffInMillies -= ((1000 * 60) * 60) * hours;
            minutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
            diffInMillies -= (1000 * 60) * minutes;
            seconds = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        Settings settings = BetterRTP.getInstance().getSettings();
        String time_str = settings.getPlaceholder_timeFormat();
        if (time_str.contains("%d"))
            time_str = time_str.replace("%d", settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(days)));
        if (time_str.contains("%h"))
            time_str = time_str.replace("%h", settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(hours)));
        if (time_str.contains("%m"))
            time_str = time_str.replace("%m", settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(max)));
        if (time_str.contains("%s"))
            time_str = time_str.replace("%s", settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(seconds)));

        return time_str;
    }
}
