package com.wheezygold.happybot.sql;

import com.wheezygold.happybot.sql.exceptions.NoDataCollectedException;
import com.wheezygold.happybot.sql.exceptions.NoTableInformedException;

import java.util.HashMap;
import java.util.Map;

public class CollectedData {
    private String table;
    private Map<String, Object> data = new HashMap<>();

    public void table(String table) {
        this.table = table;
    }

    public String table() {
        return table;
    }

    public void data(String field, Object value) {
        data.put(field, value);
    }

    public Map<String, Object> data() {
        return data;
    }

    public void validate() {
        if(table == null) throw new NoTableInformedException();
        if(data.size() == 0) throw new NoDataCollectedException();
    }
}
