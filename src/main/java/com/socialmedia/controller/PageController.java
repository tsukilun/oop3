package com.socialmedia.controller;

import com.socialmedia.service.PostService;
import com.socialmedia.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PageController {

    private final UserService userService;
    private final PostService postService;

    public PageController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    /** main page */
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.getUserCount());
        model.addAttribute("postCount", postService.getPostCount());
        model.addAttribute("totalLikes", postService.getTotalLikes());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("posts", postService.getAllPosts());
        return "index";
    }

    /** search res */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        model.addAttribute("results", postService.searchPosts(keyword));
        model.addAttribute("userCount", userService.getUserCount());
        model.addAttribute("postCount", postService.getPostCount());
        model.addAttribute("totalLikes", postService.getTotalLikes());
        return "index";
    }
}
