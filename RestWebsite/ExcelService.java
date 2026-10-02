package cbse;

import java.sql.*;
import javax.naming.*;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/excel")
public class ExcelService {

    private Connection getConnection() throws Exception {
        InitialContext ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("tindi");
        return ds.getConnection();
    }

    @GET
    @Path("/students")
    public Response all() throws Exception {
        StringBuilder sb = new StringBuilder(
            "ID\tName\tClass\tMaths\tScience\tEnglish\tHindi\tSST\tTotal\t%\tGrade\n");
        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("SELECT * FROM cbse_result ORDER BY student_id");
            rs = ps.executeQuery();
            while (rs.next()) row(sb, rs);
        } finally { close(rs, ps, c); }
        byte[] bytes = sb.toString().getBytes("UTF-8");
        return Response.ok(bytes)
                .type("application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=\"all_students.xls\"")
                .header("Content-Length", String.valueOf(bytes.length))
                .build();
    }

    @GET
    @Path("/students/{id}")
    public Response one(@PathParam("id") int id) throws Exception {
        StringBuilder sb = new StringBuilder(
            "ID\tName\tClass\tMaths\tScience\tEnglish\tHindi\tSST\tTotal\t%\tGrade\n");
        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("SELECT * FROM cbse_result WHERE student_id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) row(sb, rs);
            else sb.append("Student not found: ").append(id);
        } finally { close(rs, ps, c); }
        byte[] bytes = sb.toString().getBytes("UTF-8");
        return Response.ok(bytes)
                .type("application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=\"student_" + id + ".xls\"")
                .header("Content-Length", String.valueOf(bytes.length))
                .build();
    }

    private void row(StringBuilder sb, ResultSet rs) throws SQLException {
        sb.append(rs.getInt("student_id")).append('\t');
        sb.append(rs.getString("name")).append('\t');
        sb.append(rs.getString("class")).append('\t');
        sb.append(rs.getDouble("math_marks")).append('\t');
        sb.append(rs.getDouble("science_marks")).append('\t');
        sb.append(rs.getDouble("english_marks")).append('\t');
        sb.append(rs.getDouble("hindi_marks")).append('\t');
        sb.append(rs.getDouble("sst_marks")).append('\t');
        sb.append(rs.getDouble("total_marks")).append('\t');
        sb.append(rs.getDouble("percentage")).append('\t');
        sb.append(rs.getString("grade")).append('\n');
    }

    private void close(ResultSet rs, PreparedStatement ps, Connection c) {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (ps != null) ps.close(); } catch (Exception e) {}
        try { if (c  != null) c.close();  } catch (Exception e) {}
    }
}