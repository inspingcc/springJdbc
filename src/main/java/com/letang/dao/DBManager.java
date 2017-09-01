package com.letang.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class DBManager {
    private DBManager() {
    }

    private static DBManager instance = new DBManager();

    public static DBManager getInstance() {
        return instance;
    }

    private Map<String, List<String>> TABLE_FIELDS = new HashMap<String, List<String>>();
    private ApplicationContext context = null;
    private AppDao appDao = null;

    public void init() {
        System.out.println();
        System.out.println(">>>>>>>> START DATABASE DAO   <<<<<<<<");
        context = new FileSystemXmlApplicationContext("conf/applicationContext.xml");
        appDao = (AppDao) context.getBean("AppDAO");
        try {
            List<Map<String, Object>> lis = appDao.getJdbcTemplate().queryForList("show tables");
            for (int i = 0; i < lis.size(); i++) {
                Map<String, Object> obj = lis.get(i);
                for (Object table : obj.values()) {
                    String tableName = table.toString();
                    TABLE_FIELDS.put(tableName, getFieldOfTable(tableName));
                    System.out.println("get the fields of the table whoes name is <" + tableName + ">");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getFields(String table) {
        return TABLE_FIELDS.get(table);
    }

    public List<String> getFieldOfTable(String table) {
        List<Map<String, Object>> list = appDao.getJdbcTemplate().queryForList("desc " + table);
        List<String> fields = new ArrayList<String>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            fields.add(map.get("Field").toString());
        }
        return fields;
    }

    public AppDao getGameDao() {
        return appDao;
    }
//
//	public <T extends DaoData> DaoData getDaoData(Class<? extends DaoData> class1, String table, Object... params) {
//		try {
//			DaoData daoData = class1.newInstance();
//			daoData.loadFromData(getGameDao().getData(table, params));
//			return daoData;
//		} catch (InstantiationException | IllegalAccessException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
