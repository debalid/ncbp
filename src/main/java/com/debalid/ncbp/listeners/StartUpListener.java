package com.debalid.ncbp.listeners;

import com.debalid.ncbp.Configuration;
import org.flywaydb.core.Flyway;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * Startup listener that implements some FlyWay migrations.
 * Created by debalid on 20.04.2016.
 */
@javax.servlet.annotation.WebListener
public class StartUpListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    // Public constructor is required by servlet spec
    public StartUpListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        // FlyWay migrations
        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup(Configuration.get("datasource.jndi"));
            if (dataSource == null) throw new RuntimeException("Cannot find suitable data source :(");

            Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            flyway.migrate();
        } catch (NamingException e) {
            sce.getServletContext().log("Cannot establish connection to the database", e);
        }

    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
      /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
      /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
    }
}
