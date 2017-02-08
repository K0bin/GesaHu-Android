package rhedox.gesahuvertretungsplan.test.localUnitTest;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Robin on 21.05.2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class AbbreviationResolverTest {
    @Mock
    Context context;
    @Mock
    Resources resources;

    @Test
    public void testSubjects() {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);
        when(context.getResources().getIdentifier("subjects_ch", "string", null)).thenReturn(R.string.subjects_ch);
        when(context.getResources().getIdentifier("subjects_spo", "string", null)).thenReturn(R.string.subjects_spo);
        when(context.getResources().getIdentifier("subjects_m", "string", null)).thenReturn(R.string.subjects_m);
        when(context.getResources().getIdentifier("subjects_int", "string", null)).thenReturn(R.string.subjects_int);
        when(context.getResources().getIdentifier("subjects_vert", "string", null)).thenReturn(R.string.subjects_vert);
        when(context.getString(R.string.subjects_ch)).thenReturn("Chemie");
        when(context.getString(R.string.subjects_spo)).thenReturn("Sport");
        when(context.getString(R.string.subjects_m)).thenReturn("Mathematik");
        when(context.getString(R.string.subjects_int)).thenReturn("Integration");
        when(context.getString(R.string.subjects_vert)).thenReturn("Vertretungsreserve");

        AbbreviationResolver resolve = new AbbreviationResolver(context);

        assertEquals("Subject 1", "Chemie", resolve.resolveSubject("CH"));
        assertEquals("Subject 2", "Sport", resolve.resolveSubject("sPo"));
        assertEquals("Subject 3", "Mathematik", resolve.resolveSubject("m"));
        assertEquals("Subject 4", "Integration", resolve.resolveSubject("InT"));
        assertEquals("Subject 5", "Vertretungsreserve", resolve.resolveSubject("vert"));
    }

    @Test
    public void testTeachers() {
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getResources()).thenReturn(resources);
        when(context.getResources().getIdentifier("teachers_ro", "string", null)).thenReturn(R.string.teachers_ro);
        when(context.getResources().getIdentifier("teachers_ke", "string", null)).thenReturn(R.string.teachers_ke);
        when(context.getResources().getIdentifier("teachers_xham", "string", null)).thenReturn(R.string.teachers_xham);
        when(context.getResources().getIdentifier("teachers_rue", "string", null)).thenReturn(R.string.teachers_rue);
        when(context.getString(R.string.teachers_ro)).thenReturn("Rosch");
        when(context.getString(R.string.teachers_ke)).thenReturn("Keneder");
        when(context.getString(R.string.teachers_xham)).thenReturn("X-Schinken");
        when(context.getString(R.string.teachers_rue)).thenReturn("Rück");

        AbbreviationResolver resolve = new AbbreviationResolver(context);

        assertEquals("Teacher 1", "Rosch", resolve.resolveTeacher("ro"));
        assertEquals("Teacher 2", "Keneder", resolve.resolveTeacher("kE"));
        assertEquals("Teacher 3", "X-Schinken", resolve.resolveTeacher("xHaM"));
        assertEquals("Teacher 4", "Rück", resolve.resolveTeacher("rü"));
    }
}
