package com.qprovep.exlog.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.qprovep.exlog.data.dao.CategoryDao;
import com.qprovep.exlog.data.dao.ExerciseTemplateDao;
import com.qprovep.exlog.data.dao.SessionDao;
import com.qprovep.exlog.data.dao.SetLogDao;
import com.qprovep.exlog.data.dao.WorkoutExerciseDao;
import com.qprovep.exlog.data.dao.WorkoutTemplateDao;
import com.qprovep.exlog.data.entity.Category;
import com.qprovep.exlog.data.entity.ExerciseTemplate;
import com.qprovep.exlog.data.entity.Session;
import com.qprovep.exlog.data.entity.SetLog;
import com.qprovep.exlog.data.entity.WorkoutExercise;
import com.qprovep.exlog.data.entity.WorkoutTemplate;

@Database(entities = {
        ExerciseTemplate.class,
        WorkoutTemplate.class,
        WorkoutExercise.class,
        Session.class,
        SetLog.class,
        Category.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ExerciseTemplateDao exerciseTemplateDao();

    public abstract WorkoutTemplateDao workoutTemplateDao();

    public abstract WorkoutExerciseDao workoutExerciseDao();

    public abstract SessionDao sessionDao();

    public abstract SetLogDao setLogDao();

    public abstract CategoryDao categoryDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "exlog_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
