package rhedox.gesahuvertretungsplan.ui.activity

import org.joda.time.LocalDate

/**
 * Created by robin on 07.02.2017.
 */
interface NavigationActivity {
    fun navigateToSettings()
    fun navigateToAbout()
    fun navigateToIntro()
    fun navigateToBoard(boardId: Long)
    fun navigateToSubstitutes(date: LocalDate?)
}