package com.cingsoft.cloud.helper.dbase.config;

public class BaseConfig {

    public String dbConnection = "drivers=com.mysql.jdbc.Driver\n" +
                "mysql.driver= com.mysql.jdbc.Driver\n" +
                "mysql.url =jdbc:mysql://localhost:3306/ihotel?useSSL=false&autoReconnect=true\n" +
                "mysql.username = root\n" +
                "mysql.password =\n"+
                "mysql.maxconn=5";
}
