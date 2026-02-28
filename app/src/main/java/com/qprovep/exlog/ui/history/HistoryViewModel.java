package com.qprovep.exlog.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.qprovep.exlog.data.AppDatabase;
import com.qprovep.exlog.data.dao.SessionDao;
import com.qprovep.exlog.data.entity.Session;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends AndroidViewModel {

    private final SessionDao sessionDao;
    private final androidx.lifecycle.MutableLiveData<Long> selectedDate = new androidx.lifecycle.MutableLiveData<>(
            null);
    private final LiveData<List<SessionDao.SessionHistoryItem>> sessionHistory;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        sessionDao = db.sessionDao();

        sessionHistory = androidx.lifecycle.Transformations.switchMap(selectedDate, date -> {
            if (date == null) {
                return sessionDao.getSessionHistory();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long startOfDay = cal.getTimeInMillis();

                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                long endOfDay = cal.getTimeInMillis();

                return sessionDao.getSessionHistoryByDateRange(startOfDay, endOfDay);
            }
        });
    }

    public void setSelectedDate(Long dateMillis) {
        selectedDate.setValue(dateMillis);
    }

    public Long getSelectedDate() {
        return selectedDate.getValue();
    }

    public LiveData<List<SessionDao.SessionHistoryItem>> getSessionHistory() {
        return sessionHistory;
    }

    public void deleteSession(int sessionId) {
        executor.execute(() -> {
            Session s = sessionDao.getById(sessionId);
            if (s != null) {
                sessionDao.delete(s);
            }
        });
    }
}
