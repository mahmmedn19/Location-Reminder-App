package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

// COMPLETED-TODO: Create a fake data source to act as a double to the real data source
    private var returnsError = false

    fun setReturnsError(value: Boolean) {
        returnsError = value
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return try {
            if (returnsError) {
                 throw Exception("Error happened")
             }
            return Result.Success(ArrayList(reminders))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val chosenReminder = reminders.find { it.id == id }
        return try {
            if (returnsError) {
                throw Exception("Error happened")
            }
            if (chosenReminder!= null  ){
                Result.Success(chosenReminder)
            }else{
                Result.Error("Reminder not found!")
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
        }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}