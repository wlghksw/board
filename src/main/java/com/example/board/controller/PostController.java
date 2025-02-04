package com.example.board.controller;

import com.example.board.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 기본 페이지 방 있음
    @GetMapping("/")
    public String defaultPageRedirect() {
        return "redirect:/board/rooms";
    }

    // 방 있음
    @GetMapping("/board/rooms")
    public String listRooms(Model model) {
        model.addAttribute("posts", postService.getPostsByCategory("방 있음"));
        return "roomList";
    }

    // 방 없음
    @GetMapping("/board/no-rooms")
    public String listNoRooms(Model model) {
        model.addAttribute("posts", postService.getPostsByCategory("방 없음"));
        return "noRoomList";
    }
}
