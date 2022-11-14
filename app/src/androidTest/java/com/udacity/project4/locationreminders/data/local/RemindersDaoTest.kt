package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


//// COMPLETED- TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDB() = database.close()

    @Test
    fun insertRemindersTest() = runBlocking {

        // GIVEN - insert a reminder
        val reminder = ReminderDTO("title", "description", "location", 27.80, 30.86)

        database.reminderDao().saveReminder(reminder)
        // WHEN - Get the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values

        assertThat(loaded, `is`(reminder))

    }

    @Test
    fun noReminderForDeleted() = runBlocking {

        val reminder = ReminderDTO("title", "description", "location", 27.80, 30.86)
        val id = reminder.id

        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()

        val result = database.reminderDao().getReminderById(id)

        assertThat(result, `is`(nullValue()))
    }
}