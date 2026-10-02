# CBSE Result Portal — REST API Edition 🎓

A **RESTful Java web application** for managing CBSE student results, built with **JAX-RS (Jersey), JSON, AJAX, and JDBC on Oracle XE**, deployed on **Oracle WebLogic Server**.

> 💡 **note:** This is the **third iteration** of my CBSE Result Portal. It started as servlet + JDBC, evolved to JPA, and this version refactors to a **modern REST API architecture** with a JavaScript frontend — the way modern web apps are actually built.

---

## 📖 Overview

The **CBSE Result Portal (REST Edition)** separates the backend and frontend cleanly:

- **Backend:** JAX-RS (Jersey) REST services returning JSON
- **Frontend:** Static HTML pages with vanilla JavaScript using `XMLHttpRequest`
- **Database:** Oracle XE via JNDI DataSource (`tindi`)
- **Server:** Oracle WebLogic Server 12.1.1

This architecture mirrors how modern web apps work — the server exposes HTTP endpoints and the frontend talks to them over AJAX.

**Built to practice:**
- **JAX-RS / Jersey** — `@Path`, `@GET`, `@POST`, `@Produces`, `@Consumes`, `@FormParam`, `@PathParam`
- **JSON serialization** — POJO mapping with JAXB (`@XmlRootElement`)
- **RESTful design** — resource-based URLs, proper HTTP verbs, status codes (200, 401, 404, 409, 500)
- **AJAX frontend** — XHR calls with form-encoded payloads and JSON responses
- **Session & cookies** — session-based auth with remember-me
- **WebLogic deployment** — `prefer-application-packages` to load Jersey ahead of server libraries

---

## ✨ Features

- 🔐 **User registration & login** via REST endpoints
- 🍪 **Remember Me** — cookie-based persistent login
- 🚪 **Session-based logout**
- 📊 **Full CRUD** for students via REST:
  - `POST /rest/students` — Add
  - `GET /rest/students` — List all
  - `GET /rest/students/{id}` — Search
  - `POST /rest/students/update/{id}` — Update
  - `POST /rest/students/delete/{id}` — Delete
- 📥 **Excel export** via `GET /rest/excel/students` and `/students/{id}`
- 🧮 **Automatic grade calculation** (`GradeUtil`)
- 🚨 **Friendly error messages** — duplicate ID → 409, not-found → 404, unauthorized → 401
- 📧 **Email notifications** on registration (JavaMail)
- 🧩 **Clean separation** — Java classes are services + POJOs, HTML+JS are views
- ✅ **Guarded index** — checks `/rest/users/check` on load; redirects to login if unauthenticated

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java (JDK 1.6) |
| REST framework | **JAX-RS 1.x (Jersey)** |
| JSON | JAXB POJO mapping (Jersey POJOMappingFeature) |
| Frontend | HTML5 + vanilla JavaScript (XMLHttpRequest) |
| Database | Oracle XE (JDBC + JNDI DataSource) |
| Server | Oracle WebLogic Server 12.1.1 |
| Email | JavaMail |
| Build | Manual compile + `jar` WAR packaging |

---

## 📂 Project Structure

```
cbse-rest-api-portal/
├── src/cbse/
│   ├── Student.java               → POJO with @XmlRootElement
│   ├── User.java                  → POJO with @XmlRootElement
│   ├── GradeUtil.java             → Grade calculation
│   ├── Email.java                 → JavaMail sender
│   ├── StudentService.java        → @Path("/students")
│   ├── UserService.java           → @Path("/users")
│   └── ExcelService.java          → @Path("/excel")
├── WebContent/
│   ├── *.html                     → 9 HTML pages (login, register, etc.)
│   └── WEB-INF/
│       ├── web.xml                → Jersey servlet config
│       ├── weblogic.xml           → prefer-application-packages
│       └── lib/                   → Required JARs (not committed)
├── path1.bat                      → Environment setup script
└── README.md
```

---

## 🔗 REST API Reference

### Users

| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| POST | `/rest/users/register` | `username`, `password`, `email` | 200 / 409 |
| POST | `/rest/users/login` | `username`, `password`, `remember` | 200 / 401 |
| POST | `/rest/users/logout` | — | 200 |
| GET | `/rest/users/check` | — | 200 / 401 |

### Students

| Method | Endpoint | Response |
|--------|----------|----------|
| GET | `/rest/students` | JSON array of all students |
| GET | `/rest/students/{id}` | Student JSON or 404 |
| POST | `/rest/students` | Add — form params — 200 / 409 |
| POST | `/rest/students/update/{id}` | Update marks — 200 / 404 |
| POST | `/rest/students/delete/{id}` | Delete — 200 / 404 |

### Excel

| Method | Endpoint | Response |
|--------|----------|----------|
| GET | `/rest/excel/students` | `.xls` file with all students |
| GET | `/rest/excel/students/{id}` | `.xls` for one student |

---

## 🗄️ Database Schema

```sql
CREATE TABLE users (
    username VARCHAR2(50) PRIMARY KEY,
    password VARCHAR2(100) NOT NULL,
    email    VARCHAR2(100) NOT NULL
);

CREATE TABLE cbse_result (
    student_id    NUMBER PRIMARY KEY,
    name          VARCHAR2(100) NOT NULL,
    class         VARCHAR2(20) NOT NULL,
    math_marks    NUMBER,
    science_marks NUMBER,
    english_marks NUMBER,
    hindi_marks   NUMBER,
    sst_marks     NUMBER,
    total_marks   NUMBER,
    percentage    NUMBER,
    grade         VARCHAR2(5)
);
```

---

## ⚙️ Setup & Deployment

### Prerequisites
- **JDK 1.6** (WebLogic 12.1.1 bundled JDK)
- **Oracle WebLogic Server 12.1.1**
- **Oracle XE** (running on `localhost:1521`)

### Required JARs (WEB-INF/lib)

Because Oracle licensing doesn't allow redistribution of their JDBC driver, JARs are **not** committed. Download and place in `WebContent/WEB-INF/lib/`:

| JAR | Purpose |
|-----|---------|
| `jersey-bundle-1.19.jar` | Jersey REST framework |
| `jsr311-api-1.1.1.jar` | JAX-RS API |
| `jackson-core-asl-1.9.x.jar` | JSON serialization |
| `jackson-mapper-asl-1.9.x.jar` | JSON serialization |
| `jettison-1.3.x.jar` | JSON binding |
| `mail.jar` | JavaMail |
| `activation.jar` | JavaMail dependency |
| `ojdbc6.jar` | Oracle JDBC driver |

### Step 1 — Configure WebLogic JNDI DataSource

1. Open the Admin Console: `http://localhost:7001/console`
2. **Services → JDBC → Data Sources → New**
3. JNDI name: `tindi`
4. Database: Oracle XE
5. Driver: `oracle.jdbc.xa.client.OracleXADataSource`
6. URL: `jdbc:oracle:thin:@localhost:1521:XE`
7. Target to AdminServer

### Step 2 — Create Oracle tables

Run the SQL above as user `jpa` or `system`.

### Step 3 — Set email credentials as environment variables

```cmd
set MAIL_USER=yashdeepkaur20133@gmail.com
set MAIL_PASS=your_new_app_password
```

`Email.java` reads them via `System.getenv("MAIL_USER")`.

### Step 4 — Compile

```cmd
path1.bat
javac -d WebContent\WEB-INF\classes src\cbse\*.java
```

### Step 5 — Package WAR

```cmd
cd WebContent
jar -cvf ..\cbse-rest-api.war *
```

### Step 6 — Deploy

Admin Console → Deployments → Install → upload WAR → Start.

### Step 7 — Access

```
http://localhost:7001/CBSEWebsite/login.html
```

---

## 🧠 Key Concepts Demonstrated

- **RESTful API design** — resource-based URLs, HTTP verbs, status codes
- **JAX-RS annotations** — `@Path`, `@GET`, `@POST`, `@Produces`, `@Consumes`, `@FormParam`, `@PathParam`, `@Context`
- **POJO → JSON mapping** — `@XmlRootElement` + Jersey POJOMappingFeature
- **Proper HTTP semantics** — 200, 401, 404, 409, 500
- **Session & Cookie injection** — HttpServletRequest/Response as REST parameters
- **JNDI DataSource** — container-managed connection pooling
- **AJAX frontend** — XHR with form-encoded POST + JSON response parsing
- **WebLogic classloading** — `prefer-application-packages` to prioritize bundled Jersey over server-provided
- **Error handling** — SQL error code → meaningful HTTP status code
- **Excel generation on-the-fly** — tab-delimited bytes with correct MIME type

---

## 🚀 Future Enhancements

- [ ] Migrate to **JAX-RS 2.0 with Jersey 2** (modern)
- [ ] Use **Jackson JSON provider** instead of JAXB
- [ ] Add **JWT tokens** instead of session cookies
- [ ] Replace vanilla JS with **React** or **Vue**
- [ ] Add **BCrypt password hashing**
- [ ] Add **pagination** for large student lists
- [ ] Add **Swagger / OpenAPI** docs
- [ ] Add **JUnit + JerseyTest** integration tests
- [ ] Containerize with **Docker**

---

## ⚠️ Known Limitations

- **Passwords stored plaintext** in Oracle
- **JAX-RS 1.x** — JAX-RS 2.x is current
- **No authentication filter** — each endpoint manually checks session
- **No input sanitization** on names/emails — should validate before insert

Noted honestly.

---

## 👩‍💻 Author

**Yashdeep Kaur**
- 🎓 B.Tech CSE, Punjabi University, Patiala (2026)
- 💼 Java Full Stack Trainee @ CodeSquadz
- 📧 ykdeep2453@gmail.com
- 🔗 [LinkedIn](https://linkedin.com/in/yashdeep-kaur-16aa083b1)
- 🐙 [@YashdeepKaur28](https://github.com/YashdeepKaur28)

---

⭐ If you found this useful, consider giving it a star!
