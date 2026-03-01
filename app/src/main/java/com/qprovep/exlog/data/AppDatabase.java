package com.qprovep.exlog.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE workout_templates ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0");
        }
    };

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
                            .addMigrations(MIGRATION_3_4)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
