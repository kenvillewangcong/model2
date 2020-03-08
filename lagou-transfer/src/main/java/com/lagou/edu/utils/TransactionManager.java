package com.lagou.edu.utils;

import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyComponent;

import java.sql.SQLException;

@MyComponent
public class TransactionManager {

    @MyAutowired
    private ConnectionUtils connectionUtils;

    // 开启手动事务
    public void begin() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }

    // 提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }

    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}
