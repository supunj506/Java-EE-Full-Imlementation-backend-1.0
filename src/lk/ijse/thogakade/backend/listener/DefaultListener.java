/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation_1.0
 * Date    : 9/25/2024 (Wednesday)
 * Time    : 2:15 PM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.listener;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DefaultListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUrl("jdbc:mysql://localhost:3306/thogakade");
        bds.setUsername("root");
        bds.setPassword("1234");
        bds.setMaxTotal(2);
        bds.setInitialSize(2);
        servletContextEvent.getServletContext().setAttribute("cp",bds);

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
