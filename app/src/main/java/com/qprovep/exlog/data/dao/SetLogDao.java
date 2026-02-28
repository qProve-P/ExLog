package com.qprovep.exlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.qprovep.exlog.data.entity.SetLog;

import java.util.List;

@Dao
public interface SetLogDao {

    @Insert
    long insert(SetLog setLog);

    @Insert
    void insertAll(List<SetLog> setLogs);

    @Update
    void update(SetLog setLog);

    @Delete
    void delete(SetLog setLog);

    @Query("SELECT * FROM set_logs WHERE sessionId = :sessionId ORDER BY exerciseTemplateId, setNumber ASC")
    List<SetLog> getSetLogsForSession(int sessionId);

    @Query("SELECT * FROM set_logs WHERE exerciseTemplateId = :exerciseId ORDER BY sessionId, setNumber ASC")
    List<SetLog> getSetLogsForExercise(int exerciseId);

    @Query("SELECT sl.*, s.date FROM set_logs sl " +
            "INNER JOIN sessions s ON sl.sessionId = s.id " +
            "WHERE sl.exerciseTemplateId = :exerciseId " +
            "ORDER BY s.date ASC, sl.setNumber ASC")
    LiveData<List<SetLogWithDate>> getSetLogsWithDateForExercise(int exerciseId);

    @Query("SELECT sl.*, s.date FROM set_logs sl " +
            "INNER JOIN sessions s ON sl.sessionId = s.id " +
            "WHERE sl.exerciseTemplateId = :exerciseId AND s.date >= :startDateMs " +
            "ORDER BY s.date ASC, sl.setNumber ASC")
    List<SetLogWithDate> getSetLogsWithDateForExerciseSync(int exerciseId, long startDateMs);

    class SetLogWithDate {
        @androidx.room.Embedded
        public SetLog setLog;
        public long date;
    }
}
