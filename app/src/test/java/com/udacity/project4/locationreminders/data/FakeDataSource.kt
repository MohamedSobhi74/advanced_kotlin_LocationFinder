package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO


class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()): ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    override suspend fun getReminders(): com.udacity.project4.locationreminders.data.dto.Result<List<ReminderDTO>> {

        if (shouldReturnError){
            return com.udacity.project4.locationreminders.data.dto.Result.Error("Reminders not found", 404)
        }else{
            return return com.udacity.project4.locationreminders.data.dto.Result.Success(ArrayList(reminders))
        }

    }


    override suspend fun getReminder(id: String): com.udacity.project4.locationreminders.data.dto.Result<ReminderDTO> {

        if(shouldReturnError){

            return com.udacity.project4.locationreminders.data.dto.Result.Error("Error")

        }else{

            val reminder = reminders.find { it.id == id }

            return if (reminder != null) {
                com.udacity.project4.locationreminders.data.dto.Result.Success(reminder)
            } else {
                com.udacity.project4.locationreminders.data.dto.Result.Error("Reminder not found", 404)
            }

        }


    }




}