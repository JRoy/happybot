package com.wheezygold.happybot.sql;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Selector {

    private Collector collector;
    private Sql2o sql2o;
    private Object object;
    private String what;

    private Map<String, Object> where = new HashMap<String, Object>();

    public Selector(Collector collector, Sql2o sql2o, Object object) {
        this.collector = collector;
        this.sql2o = sql2o;
        this.object = object;
    }

    public Selector what(String what) {
        this.what = what;
        return this;
    }

    public Selector where(String field, Object value) {
        where.put(field, value);
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(ResultSetHandler setHandler) {
        CollectedData collectedData = new CollectedData();
        collector.collect(object, collectedData);
        collectedData.validate();

        StringBuilder select = new StringBuilder();

        select.append("SELECT ").append(what).append(" FROM ").append(collectedData.table());

        Set<Map.Entry<String, Object>> entrySet = collectedData.data().entrySet();

        if (where.size() > 0) {
            entrySet = where.entrySet();
            select.append(" WHERE ");
            for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator
                        .next();
                select.append(entry.getKey()).append(" = :f")
                        .append(entry.getKey());
                if (iterator.hasNext()) {
                    select.append(" AND ");
                }
            }
        }

        try (Connection con = sql2o.open()) {
            Query createQuery = con.createQuery(select.toString());

            entrySet = collectedData.data().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                createQuery.addParameter(entry.getKey(), entry.getValue());
            }
            entrySet = where.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                createQuery
                        .addParameter("f" + entry.getKey(), entry.getValue());
            }
            createQuery.executeAndFetch(setHandler);
        }
    }

}
