package com.qprovep.exlog.data.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.qprovep.exlog.data.entity.Session;
import com.qprovep.exlog.data.entity.SetLog;

import java.util.List;

public class SessionWithSetLogs {

    @Embedded
    public Session session;

    @Relation(parentColumn = "id", entityColumn = "sessionId")
    public List<SetLog> setLogs;
}
