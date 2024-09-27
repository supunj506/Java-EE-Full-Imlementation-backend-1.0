/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation_1.0
 * Date    : 9/28/2024 (Saturday)
 * Time    : 11:38 AM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/orderDetail")
public class OrderDetailServlet extends HttpServlet {
    Connection connection =null;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String orderId = req.getParameter("orderId");
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        try {
            connection = ((BasicDataSource)getServletContext().getAttribute("cp")).getConnection();
            PreparedStatement stm = connection.prepareStatement("select * from orderdetail where orderId = ?");
            stm.setString(1,orderId);
            ResultSet rst = stm.executeQuery();
            while (rst.next()) {
                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("itemCode",rst.getString("itemCode"));
                PreparedStatement stm1 = connection.prepareStatement("select item.description from item where code =?");
                stm1.setString(1,rst.getString("itemCode"));
                ResultSet rst1 = stm1.executeQuery();
                while (rst1.next()){
                    objectBuilder.add("description",rst1.getString("description"));
                }

                objectBuilder.add("buyQty",rst.getInt("qty"));
                objectBuilder.add("unitPrice",rst.getDouble("unitPrice"));
                arrayBuilder.add(objectBuilder.build());

            }

            resp.setContentType("application/json");
            resp.getWriter().print(arrayBuilder.build());
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
