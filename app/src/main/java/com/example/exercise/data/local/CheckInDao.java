package com.example.exercise.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.exercise.data.model.CheckInRecord;

import java.util.List;

@Dao
public interface CheckInDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CheckInRecord record);

    @Delete
    void delete(CheckInRecord record);

    @Query("SELECT * FROM check_in_records ORDER BY timestamp DESC")
    LiveData<List<CheckInRecord>> getAllRecords();

    @Query("SELECT * FROM check_in_records ORDER BY timestamp DESC")
    List<CheckInRecord> getAllRecordsSync();

    @Query("SELECT * FROM check_in_records WHERE date = :date")
    List<CheckInRecord> getRecordsByDate(String date);

    @Query("SELECT COUNT(*) FROM check_in_records")
    LiveData<Integer> getTotalCount();

    @Query("SELECT COUNT(*) FROM check_in_records WHERE date = :date")
    int getTodayCount(String date);

    @Query("SELECT COUNT(DISTINCT date) FROM check_in_records")
    int getDistinctDaysCount();

    @Query("SELECT SUM(durationMinutes) FROM check_in_records")
    int getTotalMinutes();

    @Query("SELECT SUM(calories) FROM check_in_records")
    int getTotalCalories();

    @Query("SELECT * FROM check_in_records WHERE date >= :startDate ORDER BY timestamp DESC")
    List<CheckInRecord> getRecentRecords(String startDate);
}