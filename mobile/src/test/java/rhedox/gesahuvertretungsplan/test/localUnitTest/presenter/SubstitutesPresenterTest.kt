package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

/**
 * Created by robin on 22.12.2016.
 *
class SubstitutesPresenterTest {
    val kodein = Kodein {
        bind<SharedPreferences>() with instance ( mock<SharedPreferences> {} )
        bind<BoardsRepository>() with provider { mock<BoardsRepository> {} }
        bind<SubstitutesRepository>() with provider { mock<SubstitutesRepository> {} }
        bind<SyncObserver>() with provider { mock<SyncObserver> {} }
        bind<AccountManager>() with instance ( mock<AccountManager> {} )
        bind<AvatarLoader>() with provider { mock<AvatarLoader> {} }
        bind<PermissionManager>() with provider { mock<PermissionManager> {} }
        bind<ConnectivityManager>() with provider { mock<ConnectivityManager> {} }

        bind<SubstituteFormatter>() with provider { mock<SubstituteFormatter> {} }
    }

    val date: LocalDate = LocalDate(2016,5,12)
    val list = listOf(Substitute(0, 0, "", "", "", "", "", "", false), Substitute(0, 0, "", "", "", "", "", "", false), Substitute(0, 0, "", "", "", "", "", "", false))
    val announcement = "hi"

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
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date))
        var view = StubSubstitutesView()

        presenter.attachView(view)
        assert(!view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assert(!view.isFabVisible)

        //Simulate loading empty announcement
        presenter.onAnnouncementLoaded(date, "")
        assert(!view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assert(!view.isFabVisible)

        //Simulate loading announcement
        presenter.onAnnouncementLoaded(date, announcement)
        assert(view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assert(view.isFabVisible)

        //Simulate selecting a substitute
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(!view.isFabVisible)
        view = simulateOrientationChange(presenter)
        assert(!view.isFabVisible)
        presenter.onListItemClicked(2)
        assert(view.isFabVisible)

        //Simulate swiping
        presenter.onActivePageChanged(1)
        assert(!view.isFabVisible)
        presenter.onActivePageChanged(3)
        assert(view.isFabVisible)
    }

    @Test
    fun testCab() {
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date))
        var view = StubSubstitutesView()
        presenter.attachView(view)
        assert(!view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assert(!view.isCabVisible)

        //Simulate selecting and deselecting
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assert(view.isCabVisible)
        presenter.onListItemClicked(2)
        assert(!view.isCabVisible)
        view = simulateOrientationChange(presenter)
        assert(!view.isCabVisible)

        //Simulate swiping
        presenter.onListItemClicked(2)
        assert(view.isCabVisible)
        presenter.onActivePageChanged(2)
        assert(!view.isCabVisible)
    }

    @Test
    fun testTabs() {
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date))
        val view = StubSubstitutesView()
        presenter.attachView(view)
        assert(view.currentTab == 3)
        assert(view.tabTitles[0] == "Mo. 09.05.16")
    }

    @Test
    fun testList() {
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date))
        var view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onSubstitutesLoaded(date, list)
        assert(view.listShown[3])
        view = simulateOrientationChange(presenter)
        assert(view.listShown[3])
    }

    @Test
    fun testSelection() {
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date))
        var view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(view.selected[3] == 2)
        view = simulateOrientationChange(presenter)
        assert(view.selected[3] == 2)
        presenter.onActivePageChanged(2)
        assert(view.selected[3] == null)
    }

    @Test
    fun testStateRestoring() {
        val presenter = SubstitutesPresenter(kodein, SubstitutesState(date, 2))
        val view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onActivePageChanged(3)
        presenter.onPageAttached(1)
        presenter.onPageAttached(2)
        presenter.onPageAttached(3)
        assert(view.currentTab == 3)
        assert(view.tabTitles[0] == "Mo. 09.05.16")
        assert(view.selected[3] == 2)
        assert(!view.isFabVisible)
        assert(view.isCabVisible)
    }
}*/
