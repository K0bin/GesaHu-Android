package rhedox.gesahuvertretungsplan.test.localUnitTest;

import org.junit.Test;

import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.Substitute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Robin on 21.05.2016.
 */
public class SubstituteTest {
    @Test
    public void testSubstitute() {
        Substitute substitute = new Substitute("    71    -   3", "12abd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", new Student("12","c"));
        assertTrue("Relevant", !substitute.getIsImportant());
        assertEquals("Kind: test", Substitute.KIND_TEST, substitute.getKind());
        assertEquals("StartingLesson", 71, substitute.getStartingLesson());

        Substitute relevantSubstitute = new Substitute(" 1 -  3 ", "12abd", "Rosch", "Rosch", "Stadthalle", "Raumänderung", new Student("12","d"));
        assertTrue("Relevant", relevantSubstitute.getIsImportant());
        assertEquals("Kind: room change", Substitute.KIND_ROOM_CHANGE, relevantSubstitute.getKind());
        assertEquals("StartingLesson", 1, relevantSubstitute.getStartingLesson());

        Substitute droppedSubstitute = new Substitute(" 1 -  3 ", "5adctzxcvg", "Frau Rosch", "", "Stadthalle", "eIgEnVeRaNtwOrtliches Arbeiten", new Student("5","c"));
        assertTrue("Relevant", droppedSubstitute.getIsImportant());
        assertEquals("Kind: dropped", Substitute.KIND_DROPPED, droppedSubstitute.getKind());
    }


    @Test
    public void testSubstituteOrdering() {
        Student student = new Student("12", "c");

        Substitute substitute = new Substitute("    71    -   3", "12abd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute substitute1 = new Substitute("    1    -   7", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute substitute2 = new Substitute("    7  -   7", "6a", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute substitute3 = new Substitute("5", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);
        Substitute substitute4 = new Substitute("71    -   3", "12abcd", "Herr Röeschen", "Frau Rosch", "Stadthalle", "Klausur", student);

        assertTrue("Ordering 1",substitute.compareTo(substitute1) >= 1);
        assertTrue("Ordering 2",substitute.compareTo(substitute2) >= 1);
        assertTrue("Ordering 3",substitute.compareTo(substitute3) >= 1);
        assertTrue("Ordering 4",substitute.compareTo(substitute4) >= 1);
        assertTrue("Ordering 5",substitute1.compareTo(substitute2) <= -1);
        assertTrue("Ordering 6",substitute1.compareTo(substitute3) <= -1);
        assertTrue("Ordering 7",substitute1.compareTo(substitute4) <= -1);
    }
}
