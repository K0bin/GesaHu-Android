package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.*
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection.TestAppComponent
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.SubstitutesTestRepository

/**
 * Created by robin on 22.12.2016.
 */
class SubstitutesPresenterTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val date: LocalDate = LocalDate(2016,5,12)
    private val list = listOf(Substitute(date, 0, 0, "", "", "", "", "", "", false), Substitute(date, 0, 0, "", "", "", "", "", "", false), Substitute(date, 0, 0, "", "", "", "", "", "", false))
    private val announcement = "hi"

    private fun simulateOrientationChange(presenter: SubstitutesPresenter): StubSubstitutesView {
        presenter.detachView()
        val view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onActivePageChanged(3)
        presenter.onPageAttached(1)
        presenter.onPageAttached(2)
        presenter.onPageAttached(3)
        return view;
    }

    @Test
    fun testFab() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date))
        val repository = presenter.repository as SubstitutesTestRepository
        var view = StubSubstitutesView()

        presenter.attachView(view)
        assertFalse(view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assertFalse(view.isFabVisible)

        //Simulate loading empty announcements
        repository.loadAnnouncementForDay(date).value = Announcement(date, "")
        assertFalse(view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assertFalse(view.isFabVisible)

        //Simulate loading announcements
        repository.loadAnnouncementForDay(date).value = Announcement(date, announcement)
        assertTrue(view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assertTrue(view.isFabVisible)

        //Simulate selecting a substitute
        repository.loadSubstitutesForDay(date).value = list
        presenter.onListItemClicked(2)
        assertFalse(view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assertFalse(view.isFabVisible)
        presenter.onListItemClicked(2)
        assertTrue(view.isFabVisible)

        //Simulate swiping
        presenter.onActivePageChanged(1)
        assertFalse(view.isFabVisible)
        presenter.onActivePageChanged(3)
        assertTrue(view.isFabVisible)
    }

    @Test
    fun testCab() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date))
        val repository = presenter.repository as SubstitutesTestRepository
        var view = StubSubstitutesView()
        presenter.attachView(view)
        assertFalse(view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assertFalse(view.isCabVisible)

        //Simulate selecting and deselecting
        repository.loadSubstitutesForDay(date).value = list
        presenter.onListItemClicked(2)
        assertTrue(view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assertTrue(view.isCabVisible)
        presenter.onListItemClicked(2)
        assertFalse(view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assertFalse(view.isCabVisible)

        //Simulate swiping
        presenter.onListItemClicked(2)
        assertTrue(view.isCabVisible)
        presenter.onActivePageChanged(2)
        assertFalse(view.isCabVisible)
    }

    @Test
    fun testTabs() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date))
        val view = StubSubstitutesView()
        presenter.attachView(view)
        assertEquals(3, view.currentTab)
        assertEquals("Mo. 09.05.16", view.tabTitles[0])
    }

    @Test
    fun testList() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date))
        val repository = presenter.repository as SubstitutesTestRepository
        var view = StubSubstitutesView()
        presenter.attachView(view)
        repository.loadSubstitutesForDay(date).value = list
        assertTrue(view.listShown[3])
        view = simulateOrientationChange(presenter)
        assertTrue(view.listShown[3])
    }

    @Test
    fun testSelection() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date))
        val repository = presenter.repository as SubstitutesTestRepository
        var view = StubSubstitutesView()
        presenter.attachView(view)
        repository.loadSubstitutesForDay(date).value = list
        presenter.onListItemClicked(2)
        assert(view.selected[3] == 2)
        view = simulateOrientationChange(presenter)
        assert(view.selected[3] == 2)
        presenter.onActivePageChanged(2)
        assert(view.selected[3] == null)
    }

    @Test
    fun testStateRestoring() {
        val appComponent = TestAppComponent.create()
        val presenter = SubstitutesPresenter(appComponent.substitutesComponent(), SubstitutesState(date, 2))
        val view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onActivePageChanged(3)
        presenter.onPageAttached(1)
        presenter.onPageAttached(2)
        presenter.onPageAttached(3)
        assertEquals(3, view.currentTab)
        assertEquals("Mo. 09.05.16", view.tabTitles[0])
        assertEquals(2, view.selected[3])
        assertFalse(view.isFabVisible)
        assertTrue(view.isCabVisible)
    }
}
