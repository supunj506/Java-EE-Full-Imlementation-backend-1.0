/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation(1.0)
 * Date    : 9/25/2024 (Wednesday)
 * Time    : 5:55 PM
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

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            ResultSet rst = connection.prepareStatement("select * from item").executeQuery();
            while (rst.next()) {
                JsonObjectBuilder jo = Json.createObjectBuilder();
                jo.add("code", rst.getString("code"));
                jo.add("description", rst.getString("description"));
                jo.add("unitPrice", rst.getDouble("unitPrice"));
                jo.add("qtyOnHand", rst.getInt("qtyOnHand"));

                arrayBuilder.add(jo.build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.setContentType("application/json");
        resp.getWriter().print(arrayBuilder.build());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("insert into item values (?,?,?,?)");
            stm.setString(1, jsonObject.getString("code"));
            stm.setString(2, jsonObject.getString("description"));
            stm.setDouble(3, Double.parseDouble(jsonObject.getString("unitPrice")));
            stm.setInt(4, Integer.parseInt(jsonObject.getString("qtyOnHand")));
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("delete from item where code = ?");
            stm.setString(1, req.getParameter("code"));
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsonObject = Json.createReader(req.getReader()).readObject();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update item set description=?,unitPrice=?,qtyOnHand=? where code=?");
            stm.setString(1,jsonObject.getString("description"));
            stm.setDouble(2,Double.parseDouble(jsonObject.getString("unitPrice")));
            stm.setInt(3,Integer.parseInt(jsonObject.getString("qtyOnHand")));
            stm.setString(4,jsonObject.getString("code"));
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
