package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.utils.coroutines.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeRepo: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createRepository() {
        stopKoin()

        fakeRepo = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeRepo
        )
        runBlocking { fakeRepo.deleteAllReminders() }

    }

    private fun getReminderItem(): ReminderDataItem {
        return ReminderDataItem(
            title = "title",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
    }

    @Test
    fun saveReminder() {
        val reminder = getReminderItem()
        saveReminderViewModel.saveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.showToast.getOrAwaitValue(),
            Is.`is`("Reminder Saved !")
        )
    }

    @Test
    fun saveReminder_with_noTitle() {

        val reminder = ReminderDataItem(
            title = "",
            description = "desc",
            location = "loc",
            latitude = 47.5456551,
            longitude = 122.0101731
        )

        saveReminderViewModel.validateAndSaveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.notNullValue()
        )
    }

    @Test
    fun showLoading() = runBlocking {
        val reminder = getReminderItem()
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }


    @Test
    fun saveReminder_with_nolocation() {

        val reminder = ReminderDataItem(
            title = "hey",
            description = "hey",
            location = "",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        saveReminderViewModel.validateAndSaveReminder(reminder)
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.notNullValue()
        )
    }

}