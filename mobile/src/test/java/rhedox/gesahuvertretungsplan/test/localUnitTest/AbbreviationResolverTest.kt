package rhedox.gesahuvertretungsplan.test.localUnitTest

import android.content.Context
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver

/**
 * Created by robin on 10.03.2018.
 */
class AbbreviationResolverTest {
    private val context: Context = mockk(relaxed = true)
    private val resources: Resources = mockk(relaxed = true)

    @Test
    fun subjects() {
        every { context.applicationContext } returns context
        every {context.resources} returns resources
        every {context.resources.getIdentifier("subjects_ch", "string", null)} returns R.string.subjects_ch
        every {context.resources.getIdentifier("subjects_spo", "string", null)} returns R.string.subjects_spo
        every {context.resources.getIdentifier("subjects_m", "string", null)} returns R.string.subjects_m
        every {context.resources.getIdentifier("subjects_int", "string", null)} returns R.string.subjects_int
        every {context.resources.getIdentifier("subjects_vert", "string", null)} returns R.string.subjects_vert
        every { context.getString(R.string.subjects_ch)} returns "Chemie"
        every { context.getString(R.string.subjects_spo)} returns "Sport"
        every { context.getString(R.string.subjects_m)} returns "Mathematik"
        every { context.getString(R.string.subjects_int)} returns "Integration"
        every { context.getString(R.string.subjects_vert)} returns "Vertretungsreserve"

        val resolve = AbbreviationResolver(context)

        assertEquals("Subject 1", "Chemie", resolve.resolveSubject("CH"))
        assertEquals("Subject 2", "Sport", resolve.resolveSubject("sPo"))
        assertEquals("Subject 3", "Mathematik", resolve.resolveSubject("m"))
        assertEquals("Subject 4", "Integration", resolve.resolveSubject("InT"))
        assertEquals("Subject 5", "Vertretungsreserve", resolve.resolveSubject("vert"))
    }

    @Test
    fun testTeachers() {
        every {context.applicationContext} returns context
        every {context.resources} returns resources
        every {context.resources.getIdentifier("teachers_ro", "string", null)} returns R.string.teachers_ro
        every {context.resources.getIdentifier("teachers_ke", "string", null)} returns R.string.teachers_ke
        every {context.resources.getIdentifier("teachers_rue", "string", null)} returns R.string.teachers_rue
        every {context.getString(R.string.teachers_ro)} returns "Rosch"
        every {context.getString(R.string.teachers_ke)} returns "Keneder"
        every {context.getString(R.string.teachers_rue)} returns "Rück"

        val resolve = AbbreviationResolver(context)

        assertEquals("Teacher 1", "Rosch", resolve.resolveTeacher("ro"))
        assertEquals("Teacher 2", "Keneder", resolve.resolveTeacher("kE"))
        assertEquals("Teacher 3", "X-Schinken", resolve.resolveTeacher("xHaM"))
        assertEquals("Teacher 4", "Rück", resolve.resolveTeacher("rü"))
    }
}