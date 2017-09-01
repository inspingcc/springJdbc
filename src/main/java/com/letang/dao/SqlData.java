package com.letang.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 可以对数据库数据的Map方便去数据
 * @author Dream
 *
 */
public class SqlData {
    private Map<String, Object> data = new HashMap<String, Object>(64);

    public SqlData() {}

    public SqlData(Map<String, Object> map) {
        this.data = map;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public byte getByte(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        if (val instanceof Boolean) return (byte) (((Boolean) val) ? 1 : 0);
        return ((Number) val).byteValue();
    }

    public boolean getBoolean(String key) {
        Object val = data.get(key);
        if (val == null) {
            return false;
        }
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).byteValue() > 0;
    }

    public short getShort(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        return ((Number) val).shortValue();
    }
    public int getInt(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        return ((Number) val).intValue();
    }
    public float getFloat(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        return ((Number) val).floatValue();
    }
    public long getLong(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        return ((Number) val).longValue();
    }
    public double getDouble(String key) {
        Object val = data.get(key);
        if (val == null) {
            return 0;
        }
        return ((Number) val).doubleValue();
    }
    public String getString(String key) {
        return (String) data.get(key);
    }
    public Object get(String key) {
        return data.get(key);
    }

    public long getTimestampToLong(String key, long curTime){
        @SuppressWarnings("unused")
        String date = (String)data.get(key);
        //TODO 暂时默认
        return curTime;
    }

    public void put(String key, Object val) {
        this.data.put(key, val);
    }

    public void put(String key, Boolean bool) {
        this.data.put(key, bool ? 1 : 0);
    }

    public void put(String key, boolean bool) {
        this.data.put(key, bool ? 1 : 0);
    }

    public Timestamp getTimestamp(String key) {
        return (Timestamp) data.get(key);
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    public Map<String, Object> getMap() {
        return data;
    }

    public Date getDate(String key) {
        return (Date) data.get(key);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
