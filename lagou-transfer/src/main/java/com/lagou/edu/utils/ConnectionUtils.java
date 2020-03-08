package com.lagou.edu.utils;

import com.lagou.edu.annotation.MyComponent;

import java.sql.Connection;
import java.sql.SQLException;

@MyComponent
public class ConnectionUtils {

    // 存储当前线程链接
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 从当前线程中获取链接
     */
    public Connection getCurrentThreadConn() throws SQLException {
        // 判断当前线程中是否已经绑顶连接，否则需要去数据连接池获取一个连接，并绑定当前线程
        Connection connection = threadLocal.get();
        if(connection == null){
            // 从数据连接池获取连接
            connection = DruidUtils.getInstance().getConnection();

            // 绑定连接到当前线程
            threadLocal.set(connection);
        }

        return connection;
    }
}
