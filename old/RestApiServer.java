import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class RestApiServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/users", new UsersHandler());
        server.createContext("/api/posts", new PostsHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("=== REST API Server started ===");
        System.out.println("Listening on http://localhost:" + PORT);
        System.out.println();
        System.out.println("Endpoints:");
        System.out.println("  GET    /api/users");
        System.out.println("  GET    /api/users/{id}");
        System.out.println("  POST   /api/users          body: {\"name\":\"...\",\"email\":\"...\"}");
        System.out.println("  PUT    /api/users/{id}     body: {\"name\":\"...\",\"email\":\"...\"}");
        System.out.println("  DELETE /api/users/{id}");
        System.out.println();
        System.out.println("  GET    /api/posts");
        System.out.println("  POST   /api/posts          body: {\"author_id\":1,\"content\":\"...\"}");
        System.out.println("  PUT    /api/posts/{id}     body: {\"content\":\"...\"}");
        System.out.println("  POST   /api/posts/{id}/like");
        System.out.println("  DELETE /api/posts/{id}");
        System.out.println();
        System.out.println("Press Ctrl+C to stop.");
    }

    //  USERS handler
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // CORS headers so Postman and browsers work fine
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // e.g. /api/users  or  /api/users/3

            // Handle preflight
            if ("OPTIONS".equalsIgnoreCase(method)) {
                sendResponse(exchange, 204, "");
                return;
            }

            try {
                // Parse optional ID from path: /api/users/123
                Integer id = parseIdFromPath(path, "/api/users/");

                if (id == null) {
                    // /api/users
                    switch (method.toUpperCase()) {
                        case "GET":
                            handleGetAllUsers(exchange);
                            break;
                        case "POST":
                            handleCreateUser(exchange);
                            break;
                        default:
                            sendResponse(exchange, 405, JsonHelper.error("Method not allowed"));
                    }
                } else {
                    // /api/users/{id}
                    switch (method.toUpperCase()) {
                        case "GET":
                            handleGetUserById(exchange, id);
                            break;
                        case "PUT":
                            handleUpdateUser(exchange, id);
                            break;
                        case "DELETE":
                            handleDeleteUser(exchange, id);
                            break;
                        default:
                            sendResponse(exchange, 405, JsonHelper.error("Method not allowed"));
                    }
                }
            } catch (SQLException e) {
                sendResponse(exchange, 500, JsonHelper.error("Database error: " + e.getMessage()));
            }
        }

        private void handleGetAllUsers(HttpExchange exchange) throws IOException, SQLException {
            List<User> users = DatabaseManager.getAllUsers();
            sendResponse(exchange, 200, JsonHelper.usersToJson(users));
        }

        private void handleGetUserById(HttpExchange exchange, int id) throws IOException, SQLException {
            User user = DatabaseManager.getUserById(id);
            if (user != null) {
                sendResponse(exchange, 200, JsonHelper.userToJson(user));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("User not found"));
            }
        }

        private void handleCreateUser(HttpExchange exchange) throws IOException, SQLException {
            String body = readBody(exchange);
            String name = JsonHelper.getString(body, "name");
            String email = JsonHelper.getString(body, "email");

            if (name == null || email == null) {
                sendResponse(exchange, 400, JsonHelper.error("name and email are required"));
                return;
            }

            int newId = DatabaseManager.createUser(name, email);
            sendResponse(exchange, 201, JsonHelper.idResponse(newId));
        }

        private void handleUpdateUser(HttpExchange exchange, int id) throws IOException, SQLException {
            String body = readBody(exchange);
            String name = JsonHelper.getString(body, "name");
            String email = JsonHelper.getString(body, "email");

            if (name == null || email == null) {
                sendResponse(exchange, 400, JsonHelper.error("name and email are required"));
                return;
            }

            boolean updated = DatabaseManager.updateUser(id, name, email);
            if (updated) {
                sendResponse(exchange, 200, JsonHelper.message("User updated"));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("User not found"));
            }
        }

        private void handleDeleteUser(HttpExchange exchange, int id) throws IOException, SQLException {
            boolean deleted = DatabaseManager.deleteUser(id);
            if (deleted) {
                sendResponse(exchange, 200, JsonHelper.message("User deleted"));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("User not found"));
            }
        }
    }

    //  POSTS handler
    static class PostsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                sendResponse(exchange, 204, "");
                return;
            }

            try {
                // Check for /api/posts/{id}/like
                if (path.matches("/api/posts/\\d+/like")) {
                    int id = Integer.parseInt(path.split("/")[3]);
                    handleLikePost(exchange, id);
                    return;
                }

                Integer id = parseIdFromPath(path, "/api/posts/");

                if (id == null) {
                    // /api/posts
                    switch (method.toUpperCase()) {
                        case "GET":
                            handleGetAllPosts(exchange);
                            break;
                        case "POST":
                            handleCreatePost(exchange);
                            break;
                        default:
                            sendResponse(exchange, 405, JsonHelper.error("Method not allowed"));
                    }
                } else {
                    // /api/posts/{id}
                    switch (method.toUpperCase()) {
                        case "PUT":
                            handleUpdatePost(exchange, id);
                            break;
                        case "DELETE":
                            handleDeletePost(exchange, id);
                            break;
                        default:
                            sendResponse(exchange, 405, JsonHelper.error("Method not allowed"));
                    }
                }
            } catch (SQLException e) {
                sendResponse(exchange, 500, JsonHelper.error("Database error: " + e.getMessage()));
            }
        }

        private void handleGetAllPosts(HttpExchange exchange) throws IOException, SQLException {
            List<Post> posts = DatabaseManager.getAllPosts();
            sendResponse(exchange, 200, JsonHelper.postsToJson(posts));
        }

        private void handleCreatePost(HttpExchange exchange) throws IOException, SQLException {
            String body = readBody(exchange);
            int authorId = JsonHelper.getInt(body, "author_id", -1);
            String content = JsonHelper.getString(body, "content");

            if (authorId == -1 || content == null) {
                sendResponse(exchange, 400, JsonHelper.error("author_id and content are required"));
                return;
            }

            int newId = DatabaseManager.createPost(authorId, content, 0);
            sendResponse(exchange, 201, JsonHelper.idResponse(newId));
        }

        private void handleUpdatePost(HttpExchange exchange, int id) throws IOException, SQLException {
            String body = readBody(exchange);
            String content = JsonHelper.getString(body, "content");

            if (content == null) {
                sendResponse(exchange, 400, JsonHelper.error("content is required"));
                return;
            }

            boolean updated = DatabaseManager.updatePost(id, content);
            if (updated) {
                sendResponse(exchange, 200, JsonHelper.message("Post updated"));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("Post not found"));
            }
        }

        private void handleLikePost(HttpExchange exchange, int id) throws IOException, SQLException {
            boolean liked = DatabaseManager.likePost(id);
            if (liked) {
                sendResponse(exchange, 200, JsonHelper.message("Post liked"));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("Post not found"));
            }
        }

        private void handleDeletePost(HttpExchange exchange, int id) throws IOException, SQLException {
            boolean deleted = DatabaseManager.deletePost(id);
            if (deleted) {
                sendResponse(exchange, 200, JsonHelper.message("Post deleted"));
            } else {
                sendResponse(exchange, 404, JsonHelper.error("Post not found"));
            }
        }
    }

    //  Helper methods

    /** Send JSON response */
    private static void sendResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    /** Read request body as string */
    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        byte[] bytes = is.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /** Extract numeric ID from path like /api/users/42 → 42, or null if no ID */
    private static Integer parseIdFromPath(String path, String prefix) {
        if (!path.startsWith(prefix)) return null;
        String rest = path.substring(prefix.length());
        if (rest.isEmpty() || rest.equals("/")) return null;
        // remove trailing slash
        if (rest.endsWith("/")) rest = rest.substring(0, rest.length() - 1);
        try {
            return Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
