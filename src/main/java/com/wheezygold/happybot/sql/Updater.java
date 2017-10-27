package com.wheezygold.happybot.sql;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Updater {
    private Collector collector;
    private Sql2o sql2o;
    private Object object;

    private Map<String, Object> where = new HashMap<>();

    public Updater(Collector collector, Sql2o sql2o, Object object) {
        this.collector = collector;
        this.sql2o = sql2o;
        this.object = object;
    }

    public Updater where(String field, Object value) {
        where.put(field, value);
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void execute() {
        CollectedData collectedData = new CollectedData();
        collector.collect(object, collectedData);
        collectedData.validate();

        StringBuilder update = new StringBuilder();

        update.append("UPDATE ").append(collectedData.table()).append(" SET ");

        Set<Entry<String, Object>> entrySet = collectedData.data().entrySet();
        for (Iterator iterator = entrySet.iterator(); iterator.hasNext(); ) {
            Entry<String, Object> entry = (Entry<String, Object>) iterator
                    .next();

            update.append(entry.getKey()).append(" = :").append(entry.getKey());
            if (iterator.hasNext()) {
                update.append(",");
            }
        }

        if (where.size() > 0) {
            entrySet = where.entrySet();
            update.append(" WHERE ");
            for (Iterator iterator = entrySet.iterator(); iterator.hasNext(); ) {
                Entry<String, Object> entry = (Entry<String, Object>) iterator
                        .next();
                update.append(entry.getKey()).append(" = :f")
                        .append(entry.getKey());
                if (iterator.hasNext()) {
                    update.append(" AND ");
                }
            }
        }

        try (Connection con = sql2o.open()) {
            Query createQuery = con.createQuery(update.toString());

            entrySet = collectedData.data().entrySet();
            for (Entry<String, Object> entry : entrySet) {
                createQuery.addParameter(entry.getKey(), entry.getValue());
            }
            entrySet = where.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                createQuery
                        .addParameter("f" + entry.getKey(), entry.getValue());
            }
            createQuery.executeUpdate();
        }
    }
}
