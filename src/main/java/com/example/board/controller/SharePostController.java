package com.example.board.controller;

import com.example.board.entity.SharePost;
import com.example.board.service.SharePostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/share")
public class SharePostController {

    private final SharePostService sharePostService;

    public SharePostController(SharePostService sharePostService) {
        this.sharePostService = sharePostService;
    }

    // ✅ 게시글 목록 조회
    @GetMapping("/list")
    public String getAllPosts(Model model) {
        List<SharePost> posts = sharePostService.getAllPosts();
        model.addAttribute("posts", posts);
        return "sharePostList";
    }

    // ✅ 특정 게시글 조회
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model) {
        SharePost post = sharePostService.getPostById(id);
        model.addAttribute("post", post);
        return "sharePostDetail"; // 게시글 상세 페이지로 이동
    }


    // ✅ 게시글 작성 페이지 이동
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new SharePost());
        return "sharePostCreate";
    }

    // ✅ 게시글 등록 (로그인 기능 없이 "testUser"로 자동 저장)
    @PostMapping("/create")
    public String createPost(@ModelAttribute SharePost sharePost) {
        sharePost.setUserId("testUser"); // 로그인 기능이 없으므로 "testUser"로 설정
        sharePost.setAuthorName("테스트 사용자");
        sharePostService.createPost(sharePost);
        return "redirect:/share/list";
    }

    // ✅ 게시글 수정 페이지 이동 (본인만 가능)
    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        SharePost post = sharePostService.getPostById(id);

        // 🔥 로그인 기능 없이 "testUser"만 수정 가능하도록 강제
        if (!post.getUserId().equals("testUser")) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        model.addAttribute("post", post);
        return "sharePostUpdate";
    }

    // ✅ 게시글 수정 (본인만 가능)
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long id, @ModelAttribute SharePost sharePost) {
        // 로그인한 사용자 ID (임시로 "testUser" 사용)
        String currentUserId = "testUser";

        // 본인 확인 후 수정 진행
        sharePostService.updatePost(id, sharePost, currentUserId);

        return "redirect:/share/" + id;
    }


    // ✅ 게시글 삭제 (본인만 가능)
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        sharePostService.deletePost(id, "testUser");
        return "redirect:/share/list";
    }

    // ✅ 게시글 검색
    @GetMapping("/search")
    public String searchPosts(@RequestParam String keyword, Model model) {
        List<SharePost> posts = sharePostService.searchPosts(keyword);
        model.addAttribute("posts", posts);
        return "sharePostList";
    }
}
