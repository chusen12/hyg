package it.chusen.hyg.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Map.Entry;


public class DBOper {
    public static Connection conn = null;
    public static PreparedStatement pstmt = null;
    static ResultSet rs = null;
    static String driver = "com.mysql.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3306/dorm";
    static String user = "root";
    static String password = "chusen";

    public Connection getConn() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

    public void closeAll() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String prepareSQL, Object[] param) {
        try {
            pstmt = conn.prepareStatement(prepareSQL);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]);
                }
            }
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public int executeUpdate(String prepareSQL, Object[] param) {
        int count = 0;
        try {
            pstmt = conn.prepareStatement(prepareSQL);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]);
                }
            }
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static void main(String[] args) {

        DBOper util = new DBOper();
        util.getConn();
        util.executeUpdate("insert into test values (?,?)",new Object[]{4,"jiji"});
        //util.executeUpdate("update test set name = ? where id = ?", new Object[]{"chuchen", 1});
    }


}