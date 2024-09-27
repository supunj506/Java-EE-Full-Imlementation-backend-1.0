/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation_1.0
 * Date    : 9/27/2024 (Friday)
 * Time    : 11:15 AM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
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

@WebServlet(urlPatterns = "/orders")
public class OrdersServlet extends HttpServlet {
    Connection connection = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArrayBuilder array = Json.createArrayBuilder();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            ResultSet rst = connection.prepareStatement("select * from orders").executeQuery();
            while (rst.next()) {
                JsonObjectBuilder jo = Json.createObjectBuilder();
                jo.add("orderId", rst.getString("id"));
                jo.add("orderDate", rst.getString("date"));
                jo.add("CustomerId", rst.getString("customerId"));
                array.add(jo.build());

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.setContentType("application/json");
        resp.getWriter().print(array.build());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonObject = Json.createReader(req.getReader()).readObject();
        JsonArray itemDetails = jsonObject.getJsonArray("itemDetails");
        String orderId = jsonObject.getString("orderId");
        String customerId = jsonObject.getString("customerId");
        String date = jsonObject.getString("date");
      /*  for (int i = 0; i < itemDetails.size(); i++) {
            JsonObject itemObject = itemDetails.getJsonObject(i);
            System.out.println(itemObject.getString("itemCode"));
            System.out.println(itemObject.getString("qty"));
            System.out.println(itemObject.getString("unitPrice"));
        }
*/
        try {
            connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection();

            connection.setAutoCommit(false);

            PreparedStatement stm = connection.prepareStatement("insert into orders (id, date, customerId) value (?,?,?)");
            stm.setString(1, orderId);
            stm.setString(2, date);
            stm.setString(3, customerId);
            stm.executeUpdate();

            PreparedStatement stm1 = connection.prepareStatement("insert into orderdetail(orderId, itemCode, qty, unitPrice) value (?,?,?,?)");
            for (int i = 0; i < itemDetails.size(); i++) {
                stm1.setString(1, orderId);
                stm1.setString(2, itemDetails.getJsonObject(i).getString("itemCode"));
                stm1.setInt(3, Integer.parseInt(itemDetails.getJsonObject(i).getString("qty")));
                stm1.setDouble(4, Double.parseDouble(itemDetails.getJsonObject(i).getString("unitPrice")));

                stm1.executeUpdate();
            }

            connection.commit();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
