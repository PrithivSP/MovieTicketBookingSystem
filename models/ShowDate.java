package models;

import java.time.LocalDate;
import java.util.HashMap;

public class ShowDate {
    private final String showDateId;
    private final LocalDate showDateDate;

    private final HashMap<String, ShowTime> showTimes;


    public ShowDate(String showDateId, LocalDate showDateDate, HashMap<String, ShowTime> showTimes) {
        this.showDateId = showDateId;
        this.showDateDate = showDateDate;
        this.showTimes = showTimes;
    }

    public String getShowDateId() {
        return showDateId;
    }

    public LocalDate getShowDateDate() {
        return showDateDate;
    }

    public HashMap<String, ShowTime> getShowTimes() {
        return showTimes;
    }
}
