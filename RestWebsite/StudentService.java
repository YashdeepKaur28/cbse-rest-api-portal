package cbse;

import java.sql.*;
import java.util.*;
import javax.naming.*;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/students")
public class StudentService {

    private Connection getConnection() throws Exception {
        InitialContext ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("tindi");
        return ds.getConnection();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> getAll() {
        List<Student> list = new ArrayList<Student>();
        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("SELECT * FROM cbse_result ORDER BY student_id");
            rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        finally { close(rs, ps, c); }
        return list;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOne(@PathParam("id") int id) {
        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("SELECT * FROM cbse_result WHERE student_id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return Response.ok(map(rs)).build();
            return Response.status(404).entity(new User("No student found with ID: " + id)).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally { close(rs, ps, c); }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@FormParam("id") int id,
                        @FormParam("name") String name,
                        @FormParam("class") String cls,
                        @FormParam("math") double math,
                        @FormParam("science") double science,
                        @FormParam("english") double english,
                        @FormParam("hindi") double hindi,
                        @FormParam("sst") double sst) {
        Connection c = null; PreparedStatement ps = null;
        try {
            Object[] r = GradeUtil.calculateFullResult(math, science, english, hindi, sst);
            c = getConnection();
            ps = c.prepareStatement(
                "INSERT INTO cbse_result (student_id,name,class,math_marks,science_marks," +
                "english_marks,hindi_marks,sst_marks,total_marks,percentage,grade) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, cls);
            ps.setDouble(4, math);
            ps.setDouble(5, science);
            ps.setDouble(6, english);
            ps.setDouble(7, hindi);
            ps.setDouble(8, sst);
            ps.setDouble(9, (Double) r[0]);
            ps.setDouble(10, (Double) r[1]);
            ps.setString(11, (String) r[2]);
            ps.executeUpdate();
            return Response.ok(new User("Student added successfully!")).build();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1)
                return Response.status(409).entity(new User("Student ID already exists.")).build();
            return Response.serverError().entity(new User(e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally { close(null, ps, c); }
    }

    @POST
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id,
                           @FormParam("math") double math,
                           @FormParam("science") double science,
                           @FormParam("english") double english,
                           @FormParam("hindi") double hindi,
                           @FormParam("sst") double sst) {
        Connection c = null; PreparedStatement ps = null;
        try {
            Object[] r = GradeUtil.calculateFullResult(math, science, english, hindi, sst);
            c = getConnection();
            ps = c.prepareStatement(
                "UPDATE cbse_result SET math_marks=?,science_marks=?,english_marks=?," +
                "hindi_marks=?,sst_marks=?,total_marks=?,percentage=?,grade=? WHERE student_id=?");
            ps.setDouble(1, math);
            ps.setDouble(2, science);
            ps.setDouble(3, english);
            ps.setDouble(4, hindi);
            ps.setDouble(5, sst);
            ps.setDouble(6, (Double) r[0]);
            ps.setDouble(7, (Double) r[1]);
            ps.setString(8, (String) r[2]);
            ps.setInt(9, id);
            int rows = ps.executeUpdate();
            if (rows > 0)
                return Response.ok(new User("Updated successfully!")).build();
            return Response.status(404).entity(new User("No student found with ID: " + id)).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally { close(null, ps, c); }
    }

    @POST
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        Connection c = null; PreparedStatement ps = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("DELETE FROM cbse_result WHERE student_id=?");
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) return Response.ok(new User("Deleted successfully!")).build();
            return Response.status(404).entity(new User("No student found with ID: " + id)).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally { close(null, ps, c); }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setClassName(rs.getString("class"));
        s.setMathMarks(rs.getDouble("math_marks"));
        s.setScienceMarks(rs.getDouble("science_marks"));
        s.setEnglishMarks(rs.getDouble("english_marks"));
        s.setHindiMarks(rs.getDouble("hindi_marks"));
        s.setSstMarks(rs.getDouble("sst_marks"));
        s.setTotalMarks(rs.getDouble("total_marks"));
        s.setPercentage(rs.getDouble("percentage"));
        s.setGrade(rs.getString("grade"));
        return s;
    }

    private void close(ResultSet rs, PreparedStatement ps, Connection c) {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (ps != null) ps.close(); } catch (Exception e) {}
        try { if (c  != null) c.close();  } catch (Exception e) {}
    }
}