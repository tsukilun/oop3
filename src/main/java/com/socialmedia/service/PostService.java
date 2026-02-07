package com.socialmedia.service;

import com.socialmedia.exception.BadRequestException;
import com.socialmedia.exception.ResourceNotFoundException;
import com.socialmedia.model.Post;
import com.socialmedia.repository.PostRepository;
import com.socialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(int id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id " + id + " not found"));
    }

    public int createPost(int authorId, String content) {
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Content is required");
        }
        // Check that author exists
        userRepository.findById(authorId)
                .orElseThrow(() -> new BadRequestException("Author with id " + authorId + " does not exist"));

        return postRepository.save(authorId, content.trim());
    }

    public void updatePost(int id, String content) {
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Content is required");
        }
        boolean updated = postRepository.update(id, content.trim());
        if (!updated) {
            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }
    }

    public void likePost(int id) {
        boolean liked = postRepository.like(id);
        if (!liked) {
            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }
    }

    public void deletePost(int id) {
        boolean deleted = postRepository.delete(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Post with id " + id + " not found");
        }
    }

   
    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return postRepository.findAll();
        }
        return postRepository.searchByKeyword(keyword.trim());
    }

    public int getPostCount() {
        return postRepository.count();
    }

    public int getTotalLikes() {
        return postRepository.totalLikes();
    }
}
