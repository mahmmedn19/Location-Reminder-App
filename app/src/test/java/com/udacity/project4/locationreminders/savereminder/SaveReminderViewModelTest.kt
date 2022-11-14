package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //COMPLETED-TODO: provide testing to the SaveReminderView and its live data objects
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var application: Application

    private lateinit var dataSource: FakeDataSource

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setupViewModel() {
        stopKoin()
        application = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(application)
        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(application, dataSource)
    }

    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {

        val reminder = ReminderDataItem("Title", "Description", "Location", 19.0, 20.2)

        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.validateAndSaveReminder(reminder)

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {

        val reminderNoTitle = ReminderDataItem(null, "description", "location", 27.80, 30.86)

        saveReminderViewModel.validateAndSaveReminder(reminderNoTitle)

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(Matchers.notNullValue())
        )
    }
}