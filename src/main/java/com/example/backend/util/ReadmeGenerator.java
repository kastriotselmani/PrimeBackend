package com.example.backend.util;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReadmeGenerator {

    public static void main(String[] args) throws Exception {
        Path projectRoot = Paths.get("").toAbsolutePath();
        Path readme = projectRoot.resolve("README.md");

        String md = """
      # Prime Retail&Trade Solutions Assignment (Spring Boot 3, Java 21)

      JWT authentication, role-based access (Admin/Analyst), project tree with mixed item types, and analytics aggregations (ECharts-ready).

      ## Tech Stack
      - **Java 21**, **Spring Boot 3.5.7**
      - Spring Security (stateless, JWT)
      - Lombok, Jackson
      - springdoc-openapi (Swagger UI)
      - JUnit 5, Mockito (tests)

      ---

      ## Project Structure
      ```
      src
       └─ main
          ├─ java/com/example/backend
          │  ├─ auth/                 # JWT service, Login DTOs
          │  ├─ config/               # SecurityConfig, OpenAPI, handlers
          │  ├─ domain/               # Project, TaskItem, NoteItem, AttachmentItem, Priority
          │  ├─ repo/                 # In-memory data stores (DataStore, ProjectStore)
          │  ├─ service/              # AuthService, ProjectService (RBAC pruning)
          │  ├─ analytics/            # SaleRow, AnalyticsService, AnalyticsController
          │  └─ web/                  # REST controllers (Auth, Projects, Admin)
          └─ resources
             ├─ application.properties
             └─ sales_dummy_data.json # dataset for analytics tasks
      ```

      ---

      ## Getting Started
      ```bash
      # Run tests (optional)
      mvn -q test

      # Start the app
      mvn -q spring-boot:run
      # http://localhost:8080
      ```

      ---

      ## Configuration
      Default values are embedded; you can move them to **application.yml** if you like.
      ```yaml
      server:
        port: 8080

      
      app:
        jwt:
          secret: 0123456789abcdef0123456789abcdef0123456789abcdef
          issuer: example-backend
          ttlMinutes: 120
      ```

      ---

      ## Sample Users
      | User             | Password | Roles          |
      |------------------|----------|----------------|
      | pm@demo.io       | pass     | Admin, Analyst |
      | analyst@demo.io  | pass     | Analyst        |
      | basic@example.com  | basic123 | Basic        |
      

      > Passwords are BCrypted in the seed. JWTs carry roles (e.g., `["Admin","Analyst"]`).

      ---

      ## Authentication & Security
      - **POST `/api/authenticate`** returns a JWT.
      - Include it on protected endpoints:
        ```
        Authorization: Bearer <JWT>
        ```
      - Role mapping: JWT roles → Spring authorities `ROLE_<role>`.  
      - `@EnableMethodSecurity` allows `@PreAuthorize`, demonstrated in **/api/admin/ping**.

      ---

      ## Endpoints

      ### Auth
      - `POST /api/authenticate` → `{ token, user:{id,name,roles[]} }`

      ### Who am I 
      - `GET /api/whoami` → current principal details (requires JWT)

      ### Projects (RBAC, pruning)
      - `GET /api/projects/all`  
      - `GET /api/projects/all?mode=write` (only nodes the user can **write** to)  
      Returns a **pruned tree** based on:
      - `allowedReadRoles` and `allowedWriteRoles` per project,
      - **write implies read**,
      - `"All"` in read roles means visible to everyone.

      Each project includes `items` of mixed types:
      - **task**: `title, assigneeId, reporterId, order, time, priority`
      - **note**: `text`
      - **attachment**: `url`

      ### Admin (RBAC proof)
      - `GET /api/admin/ping` → **Admin only** (`@PreAuthorize("hasRole('Admin')")`)

      ### Analytics (read-only)
      - `GET /api/analytics/task1`
      - `GET /api/analytics/task2`
      - `GET /api/analytics/task3`

      Data source: `src/main/resources/sales_dummy_data.json`.

      ---
      
      ## RBAC Expectations (with current seed)
                
      | User              | Roles            | `GET /api/projects/all`                      | `GET /api/admin/ping` |
      |-------------------|------------------|----------------------------------------------|-----------------------|
      | pm@demo.io        | Admin, Analyst   | Retail + Marketing (full pruned view)        | 200                   |
      | analyst@demo.io   | Analyst          | Retail only (Marketing hidden)               | 403                   |
      | basic@example.com | Basic            | `[]` (no roots grant Basic; children pruned) | 403                   |
      
      > Behavior is **data-driven**: to change visibility, edit a project's `allowedReadRoles`/`allowedWriteRoles` (e.g., add `"All"`).
                

      ## Analytics Tasks (Specs)

      **Task 1 — Grouped Bars**  
      - Group by **brandName** and **categoryName**.
      - Sum **salesIncVatActual**.
      - Sort categories ascending.
      - Response: `{ xAxis:{type:'category',data:[...]}, yAxis:{type:'value'}, series:[ {type:'bar',name:'<brand>',data:[...]}, ... ] }`

      **Task 2 — Pie (Top 4 categories)**  
      - Rank categories by **sum(salesIncVatActual)** and keep **top 4**.
      - Slice **value = total volume** of that category.
      - Color **alpha** ~ volume percentage within top4, **min 0.2**; emphasis alpha = base+0.2 (cap 1.0).
      - Response: `{ series:{ type:'pie', radius:'70%', data:[ {name, value, itemStyle:{normal:{color:'rgba(...)'}, emphasis:{color:'rgba(...)'}}}, ... ] } }`

      **Task 3 — Treemap (Buckets)**  
      - Bucket **salesIncVatActual** totals into `0-10`, `10-100`, `100+`.
      - Response: `{ series:[ { type:'treemap', data:[ {name:'0-10',value:...}, {name:'10-100',value:...}, {name:'100+',value:...} ] } ] }`

      ---

      ## cURL Cheatsheet
      ```bash
      # 1) Login as Admin; capture token
      TOKEN=$(curl -s -X POST http://localhost:8080/api/authenticate \\
      -H "Content-Type: application/json" \\
      -d '{"emailOrUsername":"pm@demo.io","password":"pass"}' | jq -r .token)
                
      # 2) Projects (read)
      curl -s http://localhost:8080/api/projects/all -H "Authorization: Bearer $TOKEN" | jq
                
      # 3) Projects (write)
      curl -s "http://localhost:8080/api/projects/all?mode=write" -H "Authorization: Bearer $TOKEN" | jq
         
      # 4) Admin-only
      curl -i http://localhost:8080/api/admin/ping -H "Authorization: Bearer $TOKEN"
                
      # 5) Analytics
      curl -s http://localhost:8080/api/analytics/task1 -H "Authorization: Bearer $TOKEN" | jq
      curl -s http://localhost:8080/api/analytics/task2 -H "Authorization: Bearer $TOKEN" | jq
      curl -s http://localhost:8080/api/analytics/task3 -H "Authorization: Bearer $TOKEN" | jq
      
      # Login as Basic; expect projects empty & admin 403
      BTOKEN=$(curl -s -X POST http://localhost:8080/api/authenticate \\
      -H "Content-Type: application/json" \\
      -d '{"emailOrUsername":"basic@example.com","password":"basic123"}' | jq -r .token)
      
      curl -s http://localhost:8080/api/projects/all -H "Authorization: Bearer $BTOKEN" | jq
      curl -i http://localhost:8080/api/admin/ping -H "Authorization: Bearer $BTOKEN"
      ```

      ---

      ## Swagger / OpenAPI
      - Dependency: `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0`
      - Allowed paths in security: `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
      - UI: **http://localhost:8080/swagger-ui.html**  
        Use **Authorize** to paste your JWT.

      ---

      ## Postman
      We also prepared collections you can import:
      - Story 3 (Auth + Projects)
      - Analytics (Tasks 1–3)

      (Environment sets `{{baseUrl}}` and stores `{{token}}` from the Auth request.)

      ---

      Run:
      ```bash
      mvn -q test
      ```

      ---

      ## Troubleshooting
      - **401** → Missing/expired token. Re-authenticate or check `Authorization: Bearer <JWT>`.
      - **403** → Role missing. Use an Admin token for `/api/admin/*`.
      - **Swagger not loading** → Ensure dependencies resolved and paths whitelisted.
      - **Analytics 500** → Confirm `sales_dummy_data.json` exists under `src/main/resources/` and restart.

      """;

        Files.writeString(
                readme,
                md,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        System.out.println("README.md written to: " + readme);
    }
}
