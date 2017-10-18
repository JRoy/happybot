package com.wheezygold.happybot.sql;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

public class Inserter {
    private Collector collector;
    private Sql2o sql2o;
    private Object object;

    public Inserter(Collector collector, Sql2o sql2o, Object object) {
        this.collector = collector;
        this.sql2o = sql2o;
        this.object = object;
    }

    public Long insertAndFetchKey() {
        return internalInsert(true);
    }

    public void insert() {
        internalInsert(false);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Long internalInsert(boolean fetch) {
        CollectedData data = new CollectedData();
        collector.collect(object, data);
        data.validate();

        StringBuilder insert = new StringBuilder();
        insert.append("INSERT INTO ").append(data.table()).append(" (");

        Set<Entry<String, Object>> entrySet = data.data().entrySet();

        for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
            Entry<String, Object> entry = (Entry<String, Object>) iterator
                    .next();

            insert.append(entry.getKey());
            if (iterator.hasNext()) {
                insert.append(",");
            }
        }

        insert.append(") VALUES (");
        for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
            Entry<String, Object> entry = (Entry<String, Object>) iterator
                    .next();

            insert.append(":").append(entry.getKey());
            if (iterator.hasNext()) {
                insert.append(",");
            }
        }
        insert.append(")");

        try (Connection con = sql2o.open()) {
            Query query = con.createQuery(insert.toString(), fetch);
            for (Entry<String, Object> entry : entrySet) {
                query.addParameter(entry.getKey(), entry.getValue());
            }

            Connection executeUpdate = query.executeUpdate();
            if (fetch) {
                return (Long) executeUpdate.getKey();
            }
        }

        return null;
    }

}