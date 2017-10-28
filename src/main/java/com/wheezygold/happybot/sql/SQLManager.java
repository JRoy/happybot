package com.wheezygold.happybot.sql;

import com.wheezygold.happybot.sql.exceptions.NoCollectorRegistredException;
import com.wheezygold.happybot.util.C;
import org.junit.Assert;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

public class SQLManager {

    private Map<Class, Collector> collectors = new HashMap<>();

    public static Sql2o sql2o;

    public SQLManager(String user, String password) {
        sql2o = new Sql2o("jdbc:mysql://127.0.0.1:3306/coins", "root", password);
        registerCollector(UserToken.class, ((obj, collectedData) -> {
            collectedData.table("user");

            UserToken userToken = (UserToken) obj;
            collectedData.data("coins", userToken.getCoins());
            collectedData.data("userid", userToken.getUserid());
//            collectedData.data("epoch", userToken.getEpoch());
        }));
        UserToken userToken = new UserToken();
        userToken.setCoins(69);
        userToken.setUserid("12345678910");
        Long key = insert(userToken).insertAndFetchKey();
        Assert.assertNotNull(key);
        select(userToken).what("coins").where("id", 1).execute(resultSet -> {
            while (resultSet.next()) {
                C.log(String.valueOf(resultSet.getInt(1)));
            }
            return null;
        });

    }

    public void registerCollector(Class clazz, Collector collector) {
        collectors.put(clazz, collector);
    }

    public Inserter insert(Object obj) {
        Collector collector = collectors.get(obj.getClass());
        if (collector == null) {
            throw new NoCollectorRegistredException();
        }
        return new Inserter(collector, sql2o, obj);
    }

    public Updater update(Object obj) {
        Collector collector = collectors.get(obj.getClass());
        if (collector == null) {
            throw new NoCollectorRegistredException();
        }
        return new Updater(collector, sql2o, obj);
    }

    public Selector select(Object obj) {
        Collector collector = collectors.get(obj.getClass());
        if (collector == null) {
            throw new NoCollectorRegistredException();
        }
        return new Selector(collector, sql2o, obj);
    }

}
