package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 기본페이지 방있음
    @GetMapping("/")
    public String defaultPageRedirect() {
        return "redirect:/board/rooms";
    }

    //방있음 (페이징적용)
    @GetMapping("/board/rooms")
    public String listRooms(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getPostsByCategory("방 있음", pageable);

        model.addAttribute("postsPage", postsPage);
        return "roomList";
    }

    //방없음 (페이징적용)
    @GetMapping("/board/no-rooms")
    public String listNoRooms(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getPostsByCategory("방 없음", pageable);

        model.addAttribute("postsPage", postsPage);
        return "noRoomList";
    }
}
