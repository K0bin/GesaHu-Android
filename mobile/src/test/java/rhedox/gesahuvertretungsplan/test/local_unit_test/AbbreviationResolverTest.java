package rhedox.gesahuvertretungsplan.test.local_unit_test;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Robin on 21.05.2016.
 */

@SuppressWarnings("WeakerAccess")
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
		when(context.getResources().getIdentifier("teachers_t", "string", null)).thenReturn(1);
		when(context.getResources().getIdentifier("teachers_tue", "string", null)).thenReturn(2);
		when(context.getResources().getIdentifier("teachers_tss", "string", null)).thenReturn(3);
		when(context.getString(1)).thenReturn("Teacher1");
		when(context.getString(2)).thenReturn("Teacher2");
		when(context.getString(3)).thenReturn("Teacher3");

		AbbreviationResolver resolve = new AbbreviationResolver(context);

		assertEquals("Teacher 1", "Teacher1", resolve.resolveTeacher("t"));
		assertEquals("Teacher 2", "Teacher2", resolve.resolveTeacher("tÜ"));
		assertEquals("Teacher 3", "Teacher3", resolve.resolveTeacher("Tß"));
	}
}