package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    //    C-TODO: test the navigation of the fragments.
//    C-TODO: test the displayed data on the UI.
//    C-TODO: add testing for the error messages.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()


    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = GlobalContext.get().koin.get()
        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @After
    fun stopKoinAfterTest() = stopKoin()


    @Test
    fun reminderList_clickFAB_NavigateOfTheLocationFragment() {
        // GIVEN - on ReminderList
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - click on addReminderFAB
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN verify that we navigate to SaveReminder
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
    fun twoRemindersInDB_UIshowsTwo() {
        runBlocking {
            repository.saveReminder(ReminderDTO("Title1", "Description1", "Location1",27.80, 30.86))
            repository.saveReminder(ReminderDTO("Title2", "Description2", "Location2",22.80, 29.86))
        }

        // GIVEN - on ReminderList with two Reminders
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // THEN - UI shows both Reminders
        onView(withText("Title1")).check(matches(isDisplayed()))
        onView(withText("Title1")).check(matches(isDisplayed()))
    }


    @Test
    fun noDataShows() {
        runBlocking {
            repository.deleteAllReminders()
        }
        // GIVEN - ReminderList without Reminders
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // THEN - UI shows noDataSymbol
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

}
