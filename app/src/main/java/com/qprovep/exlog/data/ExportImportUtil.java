package com.qprovep.exlog.data;

import android.content.Context;

import com.qprovep.exlog.data.dao.*;
import com.qprovep.exlog.data.entity.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExportImportUtil {

    public static String exportToJson(Context context) throws JSONException {
        AppDatabase db = AppDatabase.getInstance(context);

        JSONObject root = new JSONObject();
        root.put("version", 1);
        root.put("appName", "ExLog");

        List<Category> categories = db.categoryDao().getAllCategoriesSync();
        JSONArray catArray = new JSONArray();
        for (Category c : categories) {
            JSONObject obj = new JSONObject();
            obj.put("id", c.getId());
            obj.put("name", c.getName());
            catArray.put(obj);
        }
        root.put("categories", catArray);

        List<ExerciseTemplate> exercises = db.exerciseTemplateDao().getAllExercisesSync();
        JSONArray exArray = new JSONArray();
        for (ExerciseTemplate e : exercises) {
            JSONObject obj = new JSONObject();
            obj.put("id", e.getId());
            obj.put("name", e.getName());
            obj.put("category", e.getCategory() != null ? e.getCategory() : JSONObject.NULL);
            obj.put("note", e.getNote() != null ? e.getNote() : JSONObject.NULL);
            obj.put("exampleLink", e.getExampleLink() != null ? e.getExampleLink() : JSONObject.NULL);
            exArray.put(obj);
        }
        root.put("exercises", exArray);

        List<WorkoutTemplate> workouts = db.workoutTemplateDao().getAllWorkoutsSync();
        JSONArray wkArray = new JSONArray();
        for (WorkoutTemplate w : workouts) {
            JSONObject obj = new JSONObject();
            obj.put("id", w.getId());
            obj.put("name", w.getName());
            wkArray.put(obj);
        }
        root.put("workouts", wkArray);

        List<WorkoutExercise> workoutExercises = db.workoutExerciseDao().getAllWorkoutExercisesSync();
        JSONArray weArray = new JSONArray();
        for (WorkoutExercise we : workoutExercises) {
            JSONObject obj = new JSONObject();
            obj.put("id", we.getId());
            obj.put("workoutTemplateId", we.getWorkoutTemplateId());
            obj.put("exerciseTemplateId", we.getExerciseTemplateId());
            obj.put("orderIndex", we.getOrderIndex());
            obj.put("referenceWeight", we.getReferenceWeight());
            obj.put("targetSets", we.getTargetSets());
            obj.put("targetReps", we.getTargetReps());
            weArray.put(obj);
        }
        root.put("workoutExercises", weArray);

        List<Session> sessions = db.sessionDao().getAllSessionsSync();
        JSONArray sesArray = new JSONArray();
        for (Session s : sessions) {
            JSONObject obj = new JSONObject();
            obj.put("id", s.getId());
            obj.put("workoutTemplateId", s.getWorkoutTemplateId() != null ? s.getWorkoutTemplateId() : JSONObject.NULL);
            obj.put("date", s.getDate());
            obj.put("durationMs", s.getDurationMs());
            obj.put("notes", s.getNotes() != null ? s.getNotes() : JSONObject.NULL);
            sesArray.put(obj);
        }
        root.put("sessions", sesArray);

        List<SetLog> setLogs = db.setLogDao().getAllSetLogsSync();
        JSONArray slArray = new JSONArray();
        for (SetLog sl : setLogs) {
            JSONObject obj = new JSONObject();
            obj.put("id", sl.getId());
            obj.put("sessionId", sl.getSessionId());
            obj.put("exerciseTemplateId", sl.getExerciseTemplateId());
            obj.put("setNumber", sl.getSetNumber());
            obj.put("reps", sl.getReps());
            obj.put("weight", sl.getWeight());
            slArray.put(obj);
        }
        root.put("setLogs", slArray);

        return root.toString(2);
    }

    public static void importFromJson(Context context, String jsonString) throws JSONException {
        AppDatabase db = AppDatabase.getInstance(context);
        JSONObject root = new JSONObject(jsonString);

        db.clearAllTables();

        if (root.has("categories")) {
            JSONArray catArray = root.getJSONArray("categories");
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < catArray.length(); i++) {
                JSONObject obj = catArray.getJSONObject(i);
                Category c = new Category(obj.getString("name"));
                c.setId(obj.getInt("id"));
                categories.add(c);
            }
            db.categoryDao().insertAll(categories);
        }

        if (root.has("exercises")) {
            JSONArray exArray = root.getJSONArray("exercises");
            List<ExerciseTemplate> exercises = new ArrayList<>();
            for (int i = 0; i < exArray.length(); i++) {
                JSONObject obj = exArray.getJSONObject(i);
                ExerciseTemplate e = new ExerciseTemplate(
                        obj.getString("name"),
                        obj.isNull("category") ? null : obj.getString("category"),
                        obj.isNull("note") ? null : obj.getString("note"),
                        obj.isNull("exampleLink") ? null : obj.getString("exampleLink"));
                e.setId(obj.getInt("id"));
                exercises.add(e);
            }
            db.exerciseTemplateDao().insertAll(exercises);
        }

        if (root.has("workouts")) {
            JSONArray wkArray = root.getJSONArray("workouts");
            List<WorkoutTemplate> workouts = new ArrayList<>();
            for (int i = 0; i < wkArray.length(); i++) {
                JSONObject obj = wkArray.getJSONObject(i);
                WorkoutTemplate w = new WorkoutTemplate(obj.getString("name"));
                w.setId(obj.getInt("id"));
                workouts.add(w);
            }
            db.workoutTemplateDao().insertAll(workouts);
        }

        if (root.has("workoutExercises")) {
            JSONArray weArray = root.getJSONArray("workoutExercises");
            List<WorkoutExercise> workoutExercises = new ArrayList<>();
            for (int i = 0; i < weArray.length(); i++) {
                JSONObject obj = weArray.getJSONObject(i);
                WorkoutExercise we = new WorkoutExercise(
                        obj.getInt("workoutTemplateId"),
                        obj.getInt("exerciseTemplateId"),
                        obj.getInt("orderIndex"),
                        obj.getDouble("referenceWeight"),
                        obj.getInt("targetSets"),
                        obj.getInt("targetReps"));
                we.setId(obj.getInt("id"));
                workoutExercises.add(we);
            }
            db.workoutExerciseDao().insertAll(workoutExercises);
        }

        if (root.has("sessions")) {
            JSONArray sesArray = root.getJSONArray("sessions");
            List<Session> sessions = new ArrayList<>();
            for (int i = 0; i < sesArray.length(); i++) {
                JSONObject obj = sesArray.getJSONObject(i);
                Session s = new Session(
                        obj.isNull("workoutTemplateId") ? null : obj.getInt("workoutTemplateId"),
                        obj.getLong("date"),
                        obj.getLong("durationMs"),
                        obj.isNull("notes") ? null : obj.getString("notes"));
                s.setId(obj.getInt("id"));
                sessions.add(s);
            }
            db.sessionDao().insertAll(sessions);
        }

        if (root.has("setLogs")) {
            JSONArray slArray = root.getJSONArray("setLogs");
            List<SetLog> setLogs = new ArrayList<>();
            for (int i = 0; i < slArray.length(); i++) {
                JSONObject obj = slArray.getJSONObject(i);
                SetLog sl = new SetLog(
                        obj.getInt("sessionId"),
                        obj.getInt("exerciseTemplateId"),
                        obj.getInt("setNumber"),
                        obj.getInt("reps"),
                        obj.getDouble("weight"));
                sl.setId(obj.getInt("id"));
                setLogs.add(sl);
            }
            db.setLogDao().insertAll(setLogs);
        }
    }
}
