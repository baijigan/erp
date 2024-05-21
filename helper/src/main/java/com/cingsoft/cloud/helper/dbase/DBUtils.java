package com.cingsoft.cloud.helper.dbase;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.*;

public class DBUtils {
    public static DBConnection connManager = DBConnection.getInstance();
    public static int executeUpdate(String sql){
        Connection conn = connManager.getConnection("mysql");
        if(sql.length()<2048)System.out.println(sql);
        else System.out.println(sql.substring(0, 50)+ " ..........................");

        int n= -1;
        try {
            Statement pstmt = (Statement)conn.createStatement();
            n= pstmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        connManager.freeConnection("mysql", conn);
        return n;
    }

    public static int executeInsert(String sql){
        return executeUpdate(sql);
    }

    public static int executeDelete(String sql){
        return executeUpdate(sql);
    }

    public static JSONArray executeQuery(String sql){
        Connection conn = connManager.getConnection("mysql");
        JSONArray arr= new JSONArray();

        if(sql.length()<2048)System.out.println(sql);
        try {
            PreparedStatement pstmt = (PreparedStatement)conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            int col = rs.getMetaData().getColumnCount();
            ResultSetMetaData felds = rs.getMetaData();

            while (rs.next()) {
                JSONObject obj= new JSONObject();
                for (int i = 1; i <= col; i++) {
                    String name = felds.getColumnName(i);
                    int type= felds.getColumnType(i);
                    if(type==Types.DOUBLE || type==Types.FLOAT || type==Types.DECIMAL || type==Types.NUMERIC){
                        Double value= rs.getDouble(i);
                        if(value==null)value= 0.0;
                        obj.put(name, value);
                    }
                    else if(type==Types.INTEGER){
                        int value= rs.getInt(i);
                        obj.put(name, value);
                    }
                    else {
                        String value= rs.getString(i);
                        if(value==null)value= "";
                        obj.put(name, value);
                    }
                }

                arr.add( obj );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connManager.freeConnection("mysql", conn);
        return arr;
    }
}
