package com.letang.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AppDao extends JdbcDaoSupport {

    private AppDao() {
    }

    private Map<String, SimpleJdbcInsert> simpleJdbcInserts = new HashMap<String, SimpleJdbcInsert>();

    /**
     * 保存daoData数据(有则更新,无则插入,满足delete()则删除)
     *
     * @param daoData
     * @param data
     * @return
     */
    public Map<String, Object> saveDaoData(DaoData daoData) {
        Map<String, Object> params = new HashMap<>();
        SqlData sqlData = new SqlData();
        daoData.saveToData(sqlData);
        String table = daoData.table();
        List<String> fields = DBManager.getInstance().getFields(table);
        if (fields != null) {
            for (int i = 0; i < fields.size(); i++) {
                String key = fields.get(i);
                Object val = sqlData.getData().get(key);
                if (val != null) {
                    params.put(key, val);
                }
            }
        }
        if (params.size() > 0) {
            String[] wheres = daoData.wheres();
            if (daoData.delete()) {
                delete(table, params, wheres);
            } else {
                if (!isDataExist(table, params, wheres)) {// 数据不存在就插入
                    insertData(table, params);
                } else {
                    update(table, params, wheres);
                }
                daoData.over();
            }
        }
        return params;
    }

    /**
     * 更新数据
     *
     * @param table
     * @param params
     * @param wheres
     */
    private void update(String table, Map<String, Object> params, String... wheres) {
        List<Object> args = new ArrayList<>();
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("update ").append(table).append(" set ");
        for (String key : params.keySet()) {
            boolean isWhereStr = false;
            for (int i = 0; i < wheres.length; i++) {
                String where = wheres[i];
                if (key.equals(where)) {
                    isWhereStr = true;
                    break;
                }
            }
            if (!isWhereStr) {
                sqlbuff.append(key).append("=?").append(',');
                args.add(params.get(key));
            }
        }
        sqlbuff.deleteCharAt(sqlbuff.length() - 1);
        if (wheres.length > 0) {
            sqlbuff.append(" where 1=1");
            for (int i = 0; i < wheres.length; ++i) {
                sqlbuff.append(" and ").append(wheres[i]).append("=?");
                args.add(params.get(wheres[i]));
            }
        }
        getJdbcTemplate().update(sqlbuff.toString(), args.toArray());
    }

    public long getPrimaryKeyData(String sql) {
        return getJdbcTemplate().queryForObject(sql, Long.class);
    }

    /**
     * 删除满足wheres的数据
     *
     * @param table
     * @param params
     * @param wheres
     * @return
     */
    private boolean delete(String table, Map<String, Object> params, String... wheres) {
        List<Object> args = new ArrayList<>();
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("delete from ").append(table).append(" where 1=1");
        for (int i = 0; i < wheres.length; i++) {
            String where = wheres[i];
            if (params.get(where) == null) {
                continue;
            }
            sqlbuff.append(" and " + where + "=?");
            args.add(params.get(where));
        }
        String sql = sqlbuff.toString();
        int result = getJdbcTemplate().update(sql, args.toArray());
        return result != 0;
    }

    /**
     * 是否存在该数据
     *
     * @param table
     * @param params
     * @param wheres
     * @return
     */
    private boolean isDataExist(String table, Map<String, Object> params, String... wheres) {
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("select count(*) from ").append(table).append(" where 1=1");
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < wheres.length; i++) {
            String where = wheres[i];
            if (params.get(where) == null) {
                continue;
            }
            sqlbuff.append(" and " + where + "=?");
            args.add(params.get(where));
        }
        String sql = sqlbuff.toString();
        int count = getJdbcTemplate().queryForObject(sql, args.toArray(), Integer.class);
        return count > 0;
    }

    /**
     * 插入数据
     *
     * @param table
     * @param map
     */
    private void insertData(String table, Map<String, Object> map) {
        SimpleJdbcInsert insert = simpleJdbcInserts.get(table);
        if (insert == null) {
            List<String> fields = DBManager.getInstance().getFieldOfTable(table);
            if (fields == null) {
                System.out.println("Can't find table's fields whoes name is " + table);
                return;
            }
            String[] sfields = new String[fields.size()];
            sfields = fields.toArray(sfields);
            insert = new SimpleJdbcInsert(getDataSource()).withTableName(table).usingColumns(sfields);
            simpleJdbcInserts.put(table, insert);
        }
        synchronized (insert) {
            insert.execute(map);
        }
    }

    /**
     * 执行sql
     *
     * @param sql
     * @param args
     * @return
     */
    private List<SqlData> getSqlDatas(String sql, Object... args) {
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql, args);
        if (list != null && list.size() > 0) {
            List<SqlData> result = new ArrayList<SqlData>();
            for (Map<String, Object> lis : list) {
                result.add(new SqlData(lis));
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 获取单条数据(多条则返回第一条)
     *
     * @param table
     *            表名
     * @param params
     *            where 的参数 例如:[key,value,key1,value1] : where key=value and
     *            key1=value1
     * @return
     */
    public SqlData getData(String table, Object... params) {
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("select * from ").append(table).append(" where 1=1");
        Object[] objs = new Object[params.length / 2];
        for (int i = 0; i < params.length; i += 2) {
            sqlbuff.append(" and " + params[i] + "=?");
            objs[i / 2] = params[i + 1];
        }
        String sql = sqlbuff.toString();
        List<SqlData> list = getSqlDatas(sql, objs);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取单条数据(多条则返回第一条)
     *
     * @param table
     *            表名
     * @param conditions
     *            where 的参数
     * @return
     */
    public SqlData getData(String table, ConditionUnit... conditions) {
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("select * from ").append(table).append(" where 1=1");
        Object[] args = new Object[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            sqlbuff.append(" and " + conditions[i].getKey() + conditions[i].getOp().getValue() + "?");
            args[i] = conditions[i].getValue();
        }
        String sql = sqlbuff.toString();
        List<SqlData> list = getSqlDatas(sql, args);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取数据列表
     *
     * @param table
     *            表名
     * @param params
     *            where 的参数 例如:[key,value,key1,value1] : where key=value and
     *            key1=value1
     * @return
     */
    public List<SqlData> getDatas(String table, Object... params) {
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("select * from ").append(table).append(" where 1=1");
        Object[] objs = new Object[params.length / 2];
        for (int i = 0; i < params.length; i += 2) {
            if (params[i].toString().equals("time") || params[i].toString().equals("startTime")) {
                sqlbuff.append(" and " + params[i] + ">?");
                objs[i / 2] = params[i + 1];
                continue;
            }

            if (params[i].toString().equals("endTime")) {
                sqlbuff.append(" and " + params[i] + "<?");
                objs[i / 2] = params[i + 1];
                continue;
            }
            sqlbuff.append(" and " + params[i] + "=?");
            objs[i / 2] = params[i + 1];
        }
        String sql = sqlbuff.toString();
        return getSqlDatas(sql, objs);
    }

    /**
     * 获取数据列表
     *
     * @param table
     *            表名
     * @param params
     *            where 的参数 例如:
     * @return
     */
    public List<SqlData> getDatas(String table, ConditionUnit... conditions) {
        StringBuffer sqlbuff = new StringBuffer(256);
        sqlbuff.append("select * from ").append(table).append(" where 1=1");
        Object[] args = new Object[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            sqlbuff.append(" and " + conditions[i].getKey() + conditions[i].getOp().getValue() + "?");
            args[i] = conditions[i].getValue();
        }
        String sql = sqlbuff.toString();
        return getSqlDatas(sql, args);
    }

    // /**
    // * 通过表名获取这个表所有的数据
    // *
    // * @param table
    // * @return
    // */
    // public List<Map<String, Object>> getDatasByTableName(String table) {
    // return getDatasBySql("select * from " + table);
    // }
    //
    // public List<Map<String, Object>> getDatasBySql(String sql) {
    // List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
    // return list;
    // }
}
