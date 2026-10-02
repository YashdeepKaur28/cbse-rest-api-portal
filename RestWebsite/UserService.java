package cbse;

import java.sql.*;
import javax.naming.InitialContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UserService {

    private Connection getConnection() throws Exception {
        InitialContext ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("tindi");
        return ds.getConnection();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@FormParam("username") String username,
                             @FormParam("password") String password,
                             @FormParam("email") String email) {
        Connection c = null; PreparedStatement ps = null;
        try {
            c = getConnection();
            ps = c.prepareStatement("INSERT INTO users (username,password,email) VALUES (?,?,?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            return Response.status(409).entity(new User("Username or email already exists.")).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (c  != null) c.close();  } catch (Exception e) {}
        }
        Email.sendRegistrationEmail(email, username);
        return Response.ok(new User("Registration successful! Please login.")).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String loginInput,
                          @FormParam("password") String password,
                          @FormParam("remember") String remember,
                          @javax.ws.rs.core.Context HttpServletRequest req,
                          @javax.ws.rs.core.Context HttpServletResponse res) {
        Connection c = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            c = getConnection();
            ps = c.prepareStatement(
                "SELECT username FROM users WHERE (LOWER(username)=LOWER(?) OR LOWER(email)=LOWER(?)) AND password=?");
            ps.setString(1, loginInput);
            ps.setString(2, loginInput);
            ps.setString(3, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                String actualUser = rs.getString("username");
                HttpSession session = req.getSession(true);
                session.setAttribute("loggedInUser", actualUser);
                if ("yes".equals(remember)) {
                    Cookie cu = new Cookie("username", loginInput);
                    Cookie cp = new Cookie("password", password);
                    cu.setMaxAge(24 * 60 * 60);
                    cp.setMaxAge(24 * 60 * 60);
                    cu.setPath("/");
                    cp.setPath("/");
                    res.addCookie(cu);
                    res.addCookie(cp);
                } else {
                    Cookie[] cookies = req.getCookies();
                    if (cookies != null) {
                        for (int i = 0; i < cookies.length; i++) {
                            Cookie ck = cookies[i];
                            if ("username".equals(ck.getName()) || "password".equals(ck.getName())) {
                                ck.setMaxAge(0);
                                ck.setPath("/");
                                res.addCookie(ck);
                            }
                        }
                    }
                }
                return Response.ok(new User("Login successful! Welcome " + actualUser)).build();
            }
            return Response.status(401).entity(new User("Invalid username/email or password")).build();
        } catch (Exception e) {
            return Response.serverError().entity(new User(e.getMessage())).build();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (c  != null) c.close();  } catch (Exception e) {}
        }
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@javax.ws.rs.core.Context HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        return Response.ok(new User("Logged out.")).build();
    }

    @GET
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@javax.ws.rs.core.Context HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("loggedInUser") != null) {
            return Response.ok(new User("Logged in as: " + s.getAttribute("loggedInUser"))).build();
        }
        return Response.status(401).entity(new User("Not logged in")).build();
    }
}