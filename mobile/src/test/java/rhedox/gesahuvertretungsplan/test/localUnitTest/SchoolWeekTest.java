package rhedox.gesahuvertretungsplan.test.localUnitTest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import rhedox.gesahuvertretungsplan.model.SchoolWeek;

import static org.junit.Assert.assertEquals;

/**
 * Created by Robin on 21.05.2016.
 */
public class SchoolWeekTest {
    @Test
    public void nextDay() {
        LocalDate date = SchoolWeek.nextFrom(new DateTime(2015,6,14,0,0));
        assertEquals("Sunday to next week", 15, date.getDayOfMonth());
    }

    @Test
    public void nextDayByHour() {
        LocalDate date = SchoolWeek.nextFrom(new DateTime(2015,6,15,21,0));
        assertEquals("Sunday to next week", 16, date.getDayOfMonth());
    }
}
