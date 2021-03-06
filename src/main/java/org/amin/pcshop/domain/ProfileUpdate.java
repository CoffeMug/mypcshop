/*
 * ProfileUpdateBean.java
 *
 */
package org.amin.pcshop.domain;

import java.util.*;
import java.sql.*;

/**
 *
 * @author  Olle Eriksson
 * (Borrowed from the bookshop application)
 */

// update an existing user or create a new one

public class ProfileUpdate {
    
    private String url=null;

    public ProfileUpdate(String _url) {
        url=_url;
    }

    // store a profile in the database

    public void setProfile(Profile pb)  throws Exception{
        Connection conn =null;
        Statement stmt = null;
        int rs;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection(url);
            
            stmt = conn.createStatement();
            String sql, sql2, sql3;
            conn.setAutoCommit(false);
            sql ="UPDATE USERS SET USER_PASS = " + "'" + pb.getPassword();
	    sql += "'," + "NAME = " + "'" + pb.getName() + "',";
	    sql += "STREET_ADDRESS = " + "'" + pb.getStreet() + "',";
	    sql += "ZIP_CODE = " + "'" + pb.getZip() + "',";
            sql += "CITY = " + "'" + pb.getCity() + "',";
	    sql += "COUNTRY = " + "'" + pb.getCountry() + "'"; 
            sql += "WHERE USER_NAME = " + "'" + pb.getUser() + "'";
            rs = stmt.executeUpdate(sql);
            sql3 = "DELETE from USER_ROLES where USER_NAME =";
	    sql3 += "'" + pb.getUser() + "'";
            rs = stmt.executeUpdate(sql3);
	    HashMap<String,Boolean> a = pb.getRole();
            Set<String> k = a.keySet();
            Iterator<String> i = k.iterator();
            while (i.hasNext()) {
		String st = i.next();
		if(a.get(st)) {
		    sql2 = "INSERT INTO USER_ROLES(USER_NAME, ROLE_NAME)";
		    sql2 += "VALUES (" + "'" + pb.getUser() + "', '" + st;
		    sql2 += "')";
		    rs = stmt.executeUpdate(sql2);
		}
	    }
	    conn.commit();
	}   
	catch(SQLException sqle){
            conn.rollback();
            throw new Exception(sqle);
	}
        finally{
            try{
              stmt.close();
            }
	    catch(Exception e) {}
            try {
              conn.close();
            }
            catch(Exception e){}
        }
    }
    
    // store a new user
 
    public void setUser(Profile pb)  throws Exception{
        Connection conn =null;
        Statement stmt = null;
        int rs;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection(url);
            
            stmt = conn.createStatement();
            String sql,sql2;
            conn.setAutoCommit(false);
            sql ="INSERT INTO USERS (USER_NAME, USER_PASS, NAME,";
	    sql += "STREET_ADDRESS, ZIP_CODE, CITY, COUNTRY) VALUES ( ";
            sql += "'" + pb.getUser() + "', '" + pb.getPassword() + "', '";
	    sql += pb.getName() + "', '" + pb.getStreet() + "', '";
            sql += pb.getZip() +"', '" + pb.getCity() + "', '";
	    sql += pb.getCountry() + "')";
            rs = stmt.executeUpdate(sql);
	    HashMap<String,Boolean> a = pb.getRole();
            Set<String> k = a.keySet();
            Iterator<String> i = k.iterator();
            
            while (i.hasNext()) {
              String st = i.next();
              if(a.get(st)){
	          sql2 = "INSERT INTO USER_ROLES(USER_NAME, ROLE_NAME)";
		  sql2 += "VALUES (" + "'" + pb.getUser() + "', '" + st + "')";
		  rs = stmt.executeUpdate(sql2);
              }
            }
            conn.commit();
	}   
	catch(SQLException sqle){
            conn.rollback();
            throw new Exception(sqle);
	}
        finally {
	    try{
		stmt.close();
            }
	    catch(Exception e) {}
            try {
		conn.close();
            }
            catch(Exception e){}
        }
    }
}

