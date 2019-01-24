package rhedox.gesahuvertretungsplan.test.local_unit_test;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.Provider;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Robin on 21.05.2016.
 */
public class SimpleProvider implements Provider {
    @Override
    public DateTimeZone getZone(String s) {
        if("UTC".equals(s)) {
            return DateTimeZone.UTC;
        }

        return null;
    }

    @Override
    public Set<String> getAvailableIDs() {
        HashSet<String> set = new HashSet<String>();
        set.add("UTC");
        return set;
    }
}
