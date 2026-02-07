package com.socialmedia.controller;

import com.socialmedia.model.Post;
import com.socialmedia.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /** get all posts */
    @GetMapping
    public List<Post> getAllPosts(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return postService.searchPosts(keyword);
        }
        return postService.getAllPosts();
    }

    /** get post by id */
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    /** create post */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> body) {
        int authorId = (int) body.get("author_id");
        String content = (String) body.get("content");
        int id = postService.createPost(authorId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id", id, "message", "Post created"));
    }

    /** update post */
    @PutMapping("/{id}")
    public Map<String, String> updatePost(@PathVariable int id, @RequestBody Map<String, String> body) {
        postService.updatePost(id, body.get("content"));
        return Map.of("message", "Post updated");
    }

    /** like a post */
    @PostMapping("/{id}/like")
    public Map<String, String> likePost(@PathVariable int id) {
        postService.likePost(id);
        return Map.of("message", "Post liked");
    }

    /** delete post */
    @DeleteMapping("/{id}")
    public Map<String, String> deletePost(@PathVariable int id) {
        postService.deletePost(id);
        return Map.of("message", "Post deleted");
    }
}
