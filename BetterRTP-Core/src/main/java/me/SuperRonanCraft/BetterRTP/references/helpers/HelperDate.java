package me.SuperRonanCraft.BetterRTP.references.helpers;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.settings.Settings;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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

    /*public static void main(String[] args) {
        long min = 0L;
        long max = 11040000L;
        long diffInMillies = max - min;
        long days, hours, minutes, seconds;

        days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= (((1000 * 60) * 60) * 24) * days;
        hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= ((1000 * 60) * 60) * hours;
        minutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= (1000 * 60) * minutes;
        seconds = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        LinkedList<String> lst = new LinkedList<>();
        if (days > 0)
            lst.add("{0} Days".replace("{0}", String.valueOf(days)));
        if (hours > 0)
            lst.add("{0} Hours".replace("{0}", String.valueOf(hours)));
        if (minutes > 0)
            lst.add("{0} Mins".replace("{0}", String.valueOf(minutes)));
        if (seconds > 0)
            lst.add("{0} Secs".replace("{0}", String.valueOf(seconds)));

        StringBuilder time_str = new StringBuilder();

        for (int i = 0; i < lst.size(); i++) {
            String str = lst.get(i);
            if (lst.size() - i - 1 >= 2) {
                str += ", ";
            } else if (lst.size() - 1 - i == 1) {
                str += " and ";
            }
            time_str.append(str);
        }

        System.out.println(time_str);
    }*/

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

        LinkedList<String> lst = new LinkedList<>();
        if (days > 0)
            lst.add(settings.getPlaceholder_timeDays().replace("{0}", String.valueOf(days)));
        if (hours > 0)
            lst.add(settings.getPlaceholder_timeHours().replace("{0}", String.valueOf(hours)));
        if (minutes > 0)
            lst.add(settings.getPlaceholder_timeMinutes().replace("{0}", String.valueOf(minutes)));
        if (seconds > 0)
            lst.add(settings.getPlaceholder_timeSeconds().replace("{0}", String.valueOf(seconds)));

        StringBuilder time_str = new StringBuilder();

        for (int i = 0; i < lst.size(); i++) {
            String str = lst.get(i);
            if (lst.size() - i - 1 >= 2) {
                str += settings.getPlaceholder_timeSeparator_middle();
            } else if (lst.size() - 1 - i == 1) {
                str += settings.getPlaceholder_timeSeparator_last();
            }
            time_str.append(str);
        }

        return time_str.toString();
    }
}
