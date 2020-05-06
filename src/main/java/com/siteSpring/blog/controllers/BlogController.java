package com.sitespring.blog.controllers;

import com.sitespring.blog.models.Post;
import com.sitespring.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/blog")
    public String blogPage(Model model) {
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "blogPage";
    }

    @GetMapping("/blog/new-post")
    public String newPostPage(Model model) {
        Post post = new Post();
        model.addAttribute("post", post);
        model.addAttribute("action", "/blog/new-post");
        return "postEditorPage";
    }

    @PostMapping("/blog/new-post")
    public String newPost(@RequestParam String title, @RequestParam String anons, @RequestParam String article, Model model) {
        Post post = new Post(title, anons, article);
        postRepository.save(post);
        return "redirect:/blog";
    }

    @GetMapping("/blog/{id}")
    public String postPage(@PathVariable(value = "id") Long id, Model model) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(value -> {
            Long views = value.getViews() + 1;
            value.setViews(views);
        });

        post.ifPresent(value -> postRepository.save(value));
        post.ifPresent(value -> model.addAttribute("post", value));
        return "postPage";
    }

    @GetMapping("/blog/{id}/edit")
    public String editPost(@PathVariable(value = "id") Long id, Model model) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(value -> model.addAttribute("post", value));
        model.addAttribute("action", "/blog/" + id + "/edit");
        return "postEditorPage";
    }

    @PostMapping("/blog/{id}/edit")
    public String updatePost(@RequestParam String title, @RequestParam String anons, @RequestParam String article,
                           @PathVariable(value = "id") Long id, Model model) {
        Optional<Post> post = postRepository.findById(id);

        post.ifPresent(value -> {
            value.setTitle(title);
            value.setAnons(anons);
            value.setArticle(article);
        });

        post.ifPresent(value -> postRepository.save(value));
        return "redirect:/blog/" + id;
    }

    @GetMapping("/blog/{id}/remove")
    public String removePost(@PathVariable(value = "id") Long id, Model model) {
        postRepository.deleteById(id);
        return "redirect:/blog";
    }
}
