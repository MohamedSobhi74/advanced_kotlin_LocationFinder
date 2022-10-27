package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReminderDTOTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun addReminderAndCheckValues() = runBlockingTest {

        val reminder = getReminderData()
        database.reminderDao().saveReminder(getReminderData())
        val savedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(savedReminder as ReminderDTO, notNullValue())
        assertThat(savedReminder.id, `is`(reminder.id))
        assertThat(savedReminder.title, `is`(reminder.title))
        assertThat(savedReminder.location, `is`(reminder.location))
        assertThat(savedReminder.description, `is`(reminder.description))
        assertThat(savedReminder.longitude, `is`(reminder.longitude))
        assertThat(savedReminder.latitude, `is`(reminder.latitude))
    }


    private fun getReminderData(): ReminderDTO {
        return ReminderDTO(
            title = "Cairo",
            description = "capital of egypt",
            location = "location",
            latitude = 30.033333,
            longitude = 31.233334
        )
    }

}

