package rhedox.gesahuvertretungsplan.test.localUnitTest;

import org.junit.Test;

import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute_old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Robin on 21.05.2016.
 */
public class SubstituteTest {
    @Test
    public void testSubstitute() {
        Substitute_old substitute = new Substitute_old("    71    -   3", "12abd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", new Student("12","c"));
        assertTrue("Relevant", !substitute.getIsImportant());
        assertEquals("Kind: test", Substitute_old.KIND_TEST, substitute.getKind());
        assertEquals("StartingLesson", 71, substitute.getStartingLesson());

        Substitute_old relevantSubstitute = new Substitute_old(" 1 -  3 ", "12abd", "Rosch", "Rosch", "Stadthalle", "Raumänderung", new Student("12","d"));
        assertTrue("Relevant", relevantSubstitute.getIsImportant());
        assertEquals("Kind: room change", Substitute_old.KIND_ROOM_CHANGE, relevantSubstitute.getKind());
        assertEquals("StartingLesson", 1, relevantSubstitute.getStartingLesson());

        Substitute_old droppedSubstitute = new Substitute_old(" 1 -  3 ", "5adctzxcvg", "Frau Rosch", "", "Stadthalle", "eIgEnVeRaNtwOrtliches Arbeiten", new Student("5","c"));
        assertTrue("Relevant", droppedSubstitute.getIsImportant());
        assertEquals("Kind: dropped", Substitute_old.KIND_DROPPED, droppedSubstitute.getKind());
    }


    @Test
    public void testSubstituteOrdering() {
        Student student = new Student("12", "c");

        Substitute_old substitute = new Substitute_old("    71    -   3", "12abd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute_old substitute1 = new Substitute_old("    1    -   7", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute_old substitute2 = new Substitute_old("    7  -   7", "6a", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute_old substitute3 = new Substitute_old("5", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute_old substitute4 = new Substitute_old("71    -   3", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);

        assertTrue("Ordering 1",substitute.compareTo(substitute1) >= 1);
        assertTrue("Ordering 2",substitute.compareTo(substitute2) >= 1);
        assertTrue("Ordering 3",substitute.compareTo(substitute3) >= 1);
        assertTrue("Ordering 4",substitute.compareTo(substitute4) >= 1);
        assertTrue("Ordering 5",substitute1.compareTo(substitute2) <= -1);
        assertTrue("Ordering 6",substitute1.compareTo(substitute3) <= -1);
        assertTrue("Ordering 7",substitute1.compareTo(substitute4) <= -1);
    }
}
