package rhedox.gesahuvertretungsplan.test.localUnitTest;

import android.content.Context;
import android.content.res.Resources;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.joda.time.DateTimeZone;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.old.SubstitutesList_old;
import rhedox.gesahuvertretungsplan.model.SubstitutesListConverter;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.testng.annotations.Test;

/**
 * Created by Robin on 20.05.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubstitutesListConverterTest {
    @Mock
    Context context;
    @Mock
    Resources resources;

    @Test
    public void testConverter() {

        //Mock Android resource system
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);
        when(context.getResources().getIdentifier("subjects_bio", "string", null)).thenReturn(R.string.subjects_bio);
        when(context.getResources().getIdentifier("teachers_sa", "string", null)).thenReturn(R.string.teachers_sa);
        when(context.getString(R.string.teachers_sa)).thenReturn("Schaf");
        when(context.getString(R.string.subjects_bio)).thenReturn("Biologie");

        //Avoid JodaTime trying to use Android APIs to set the timezone
        DateTimeZone.setProvider(new SimpleProvider());

        SubstitutesListConverter converter = new SubstitutesListConverter(new ShortNameResolver(context, false), new Student("11","c"));

        String directory = System.getProperty("user.dir");
        String body;
        try {
            body = Files.toString(new File(directory, "test.html"), Charsets.UTF_8);
        } catch (IOException e) {
            body = "";
        }

        SubstitutesList_old list = converter.convert(body);
        assertTrue(list.hasSubstitutes());
        assertTrue(!list.hasAnnouncement());
        assertEquals("Subject name resolving", "Biologie 11c", list.getSubstitutes().get(14).getSubject());
        assertEquals("Teacher name resolving", "Schaf", list.getSubstitutes().get(14).getTeacher());
        assertTrue("Relevant substitute", list.getSubstitutes().get(14).getIsImportant());
    }
}
