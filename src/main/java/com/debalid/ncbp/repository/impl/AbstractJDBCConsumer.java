package com.debalid.ncbp.repository.impl;

import com.debalid.ncbp.Configuration;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Super class for JDBC-based repository implementations.
 * Provides Data Source.
 * Created by debalid on 01.05.2016.
 */
public class AbstractJDBCConsumer {
    private DataSource dataSource;

    public AbstractJDBCConsumer() throws NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup(Configuration.get("datasource.jndi"));
        if (dataSource == null) throw new RuntimeException("Cannot find suitable data source :(");
        this.dataSource = dataSource;
    }

    protected DataSource getDataSource() {
        return dataSource;
    }
}
