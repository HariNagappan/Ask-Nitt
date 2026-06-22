package com.example.asknitt.data.local

import androidx.room.TypeConverter
import com.example.asknitt.data.ProfileVisibility
import com.example.asknitt.data.model.QuestionStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromQuestionStatus(status: QuestionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toQuestionStatus(status: String): QuestionStatus {
        return QuestionStatus.valueOf(status)
    }

    @TypeConverter
    fun fromProfileVisibility(value: ProfileVisibility): String {
        return value.name
    }

    @TypeConverter
    fun toProfileVisibility(value: String): ProfileVisibility {
        return ProfileVisibility.valueOf(value)
    }
}
