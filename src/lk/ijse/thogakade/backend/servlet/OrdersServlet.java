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
        try {
            connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (req.getParameter("orderId") != null) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            String orderId = req.getParameter("orderId");
            try {
                PreparedStatement stm1 = connection.prepareStatement("select orders.customerId from orders where id=?");
                stm1.setString(1, orderId);
                ResultSet rst = stm1.executeQuery();
                while (rst.next()) {
                    objectBuilder.add("customerId", rst.getString("customerId"));
                }
                connection.close();
                resp.setContentType("application/json");
                resp.getWriter().print(objectBuilder.build());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            JsonArrayBuilder array = Json.createArrayBuilder();
            try {
                ResultSet rst = connection.prepareStatement("select * from orders").executeQuery();
                while (rst.next()) {
                    JsonObjectBuilder jo = Json.createObjectBuilder();
                    jo.add("orderId", rst.getString("id"));
                    jo.add("orderDate", rst.getString("date"));
                    jo.add("CustomerId", rst.getString("customerId"));
                    array.add(jo.build());

                }

                connection.close();
                resp.setContentType("application/json");
                resp.getWriter().print(array.build());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonObject = Json.createReader(req.getReader()).readObject();
        JsonArray itemDetails = jsonObject.getJsonArray("itemDetails");
        String orderId = jsonObject.getString("orderId");
        String customerId = jsonObject.getString("customerId");
        String date = jsonObject.getString("date");

        try {
            connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection();

            connection.setAutoCommit(false);

            PreparedStatement stm = connection.prepareStatement("insert into orders (id, date, customerId) value (?,?,?)");
            stm.setString(1, orderId);
            stm.setString(2, date);
            stm.setString(3, customerId);
            stm.executeUpdate();

            PreparedStatement stm1 = connection.prepareStatement("insert into orderdetail(orderId, itemCode, qty, unitPrice) value (?,?,?,?)");

            PreparedStatement stm2 = connection.prepareStatement("select item.qtyOnHand from item where code=?");

            PreparedStatement stm3 = connection.prepareStatement("update item set qtyOnHand=? where code=?");

            for (int i = 0; i < itemDetails.size(); i++) {

                String itemCode = itemDetails.getJsonObject(i).getString("itemCode");
                int qtyOnHand = Integer.parseInt(itemDetails.getJsonObject(i).getString("qty"));
                double unitPrice = Double.parseDouble(itemDetails.getJsonObject(i).getString("unitPrice"));


                stm1.setString(1, orderId);
                stm1.setString(2, itemCode);
                stm1.setInt(3, qtyOnHand);
                stm1.setDouble(4, unitPrice);

                stm1.executeUpdate();

                stm2.setString(1, itemCode);
                ResultSet rst = stm2.executeQuery();
                while (rst.next()) {
                    int currentQty = rst.getInt("qtyOnHand");
                    int updateQtyOnHand = currentQty - qtyOnHand;

                    stm3.setInt(1, updateQtyOnHand);
                    stm3.setString(2, itemCode);
                    stm3.executeUpdate();
                }
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
