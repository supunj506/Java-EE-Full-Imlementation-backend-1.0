/*
 * @author : xCODE
 * Project : ThogakadeJavaEEFull Implimentation_1.0
 * Date    : 9/25/2024 (Wednesday)
 * Time    : 2:23 PM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.servlet;

import lk.ijse.thogakade.backend.dto.CustomerDTO;
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

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArrayBuilder array = Json.createArrayBuilder();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {

            ResultSet rst = connection.prepareStatement("select * from customer").executeQuery();
            while (rst.next()) {
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("id", rst.getString("id"));
                object.add("name", rst.getString("name"));
                object.add("address", rst.getString("address"));
                object.add("salary", rst.getDouble("salary"));
                array.add(object.build());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.setContentType("application/json");
        resp.getWriter().print(array.build());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject jsObject = Json.createReader(req.getReader()).readObject();
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("insert into customer values (?,?,?,?)");
            stm.setString(1, jsObject.getString("id"));
            stm.setString(2, jsObject.getString("name"));
            stm.setString(3, jsObject.getString("address"));
            stm.setDouble(4, Double.parseDouble(jsObject.getString("salary")));

            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CustomerDTO customerDTO = getReadedDTO(req);
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("update customer set name=?,address=?,salary=? where id=?");
            stm.setString(4, customerDTO.getId());
            stm.setString(1, customerDTO.getName());
            stm.setString(2, customerDTO.getAddress());
            stm.setDouble(3, customerDTO.getSalary());
            boolean update = stm.executeUpdate() > 0;
/*            JsonObjectBuilder jObject = Json.createObjectBuilder();
            if (update) {
                jObject.add("state", "Done");
                jObject.add("message", "Successfully Update Customer...!");
                resp.setStatus(200);
            } else {
                jObject.add("state", "Fail");
                jObject.add("message", "No Such Customer to Update...!");
                resp.setStatus(400);
            }
            resp.getWriter().print(jObject.build());*/

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("cp")).getConnection()) {
            PreparedStatement stm = connection.prepareStatement("delete from customer where id=?");
            stm.setString(1, req.getParameter("id"));
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private CustomerDTO getReadedDTO(HttpServletRequest req) throws IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        return new CustomerDTO(jsonObject.getString("id"), jsonObject.getString("name"), jsonObject.getString("address"), Double.parseDouble(jsonObject.getString("salary")));
    }
}
