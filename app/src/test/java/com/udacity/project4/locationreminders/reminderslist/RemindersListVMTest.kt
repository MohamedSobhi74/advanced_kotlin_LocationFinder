package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.utils.coroutines.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListVMTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineMainRule = MainCoroutineRule()


    @Before
    fun initRepo() {
        stopKoin()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        fakeDataSource = FakeDataSource()
    }

    @Test
    fun showLoading_exsitData() = runBlocking {
        fakeDataSource.deleteAllReminders()
        val reminder = getReminderData()
        fakeDataSource.saveReminder(reminder)

        coroutineMainRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        coroutineMainRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(false))


    }

    @Test
    fun check_when_noData() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()

       assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }


    @Test
    fun getAllRemindersWhenUnavailable() = runBlockingTest {
        fakeDataSource.setShouldReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`("Reminders not found"))
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