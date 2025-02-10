package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/board")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String defaultPageRedirect() {
        return "redirect:/board/rooms";
    }

    //방있음
    @GetMapping("/rooms")
    public String listRooms(@RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getPostsByCategory("방 있음", pageable);

        model.addAttribute("postsPage", postsPage);
        return "roomList";
    }

    //방없음
    @GetMapping("/no-rooms")
    public String listNoRooms(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getPostsByCategory("방 없음", pageable);

        model.addAttribute("postsPage", postsPage);
        return "noRoomList";
    }

    //글 작성
    @GetMapping("/post/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "postWrite";
    }

    //글 저장
    @PostMapping("/post/save")
    public String savePost(
            @RequestParam("rmBoardTitle") String rmBoardTitle,
            @RequestParam("postContent") String postContent,
            @RequestParam("authorRegion") String authorRegion,
            @RequestParam("category") String category,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "deposit", required = false) Integer deposit) {

        Post post = new Post();
        post.setRmBoardTitle(rmBoardTitle);
        post.setPostContent(postContent);
        post.setAuthorRegion(authorRegion);
        post.setCategory(category);
        post.setPostDate(LocalDateTime.now());
        post.setPostViews(0);

        // 로그인 기능이 없으므로 임시 값
        post.setAuthorAge(25);
        post.setAuthorName("테스트 사용자");
        post.setAuthorGender("M");
        post.setUserId("testUser"); // 임시 ID
        post.setUserPreference("default");

        //방 있음인 경우만 금액과 보증금 처리
        if ("방 있음".equals(category)) {
            post.setAmount(amount != null ? amount : 0);
            post.setDeposit(deposit != null ? deposit : 0);
        } else {
            post.setAmount(0);
            post.setDeposit(0);
        }

        postService.savePost(post);
        return "redirect:/board/" + (category.equals("방 있음") ? "rooms" : "no-rooms");
    }

    //글 조회 (방 있음 || 방 없음)
    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return "redirect:/board/rooms";
        }

        //조회수 증가
        post.setPostViews(post.getPostViews() + 1);
        postService.savePost(post);

        model.addAttribute("post", post);

        return "방 있음".equals(post.getCategory()) ? "roomView" : "noRoomView";
    }

    //글 수정
    @GetMapping("/post/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        if (post == null || !"testUser".equals(post.getUserId())) {
            return "redirect:/board/rooms"; // 본인 글이 아니면 목록으로 리다이렉트
        }

        model.addAttribute("post", post);
        return "postModify";
    }

    // 글 수정 요청 처리
    @PostMapping("/post/update")
    public String updatePost(
            @RequestParam("id") Long id,
            @RequestParam("rmBoardTitle") String rmBoardTitle,
            @RequestParam("postContent") String postContent,
            @RequestParam("authorRegion") String authorRegion,
            @RequestParam("category") String category,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "deposit", required = false) Integer deposit) {

        Post post = postService.getPostById(id);
        if (post == null || !"testUser".equals(post.getUserId())) {
            return "redirect:/board/rooms";
        }

        post.setRmBoardTitle(rmBoardTitle);
        post.setPostContent(postContent);
        post.setAuthorRegion(authorRegion);
        post.setCategory(category);

        //방 있음일 경우만 금액과 보증금 처리
        if ("방 있음".equals(category)) {
            post.setAmount(amount != null ? amount : 0);
            post.setDeposit(deposit != null ? deposit : 0);
        } else {
            post.setAmount(0);
            post.setDeposit(0);
        }

        postService.savePost(post);

        return "redirect:/board/post/" + id;
    }

    //글 삭제
    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        Post post = postService.getPostById(id);
        if (post != null && "testUser".equals(post.getUserId())) {
            postService.deletePost(id);
        }

        return (post != null && "방 있음".equals(post.getCategory())) ? "redirect:/board/rooms" : "redirect:/board/no-rooms";
    }

    //검색 기능
    @GetMapping("/search")
    public String searchPosts(@RequestParam(name = "category") String category,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            postsPage = postService.getPostsByCategory(category, pageable);
        } else {
            postsPage = postService.searchPostsByCategory(category, keyword, pageable);
        }

        model.addAttribute("postsPage", postsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);


        return category.equals("방 있음") ? "roomList" : "noRoomList";
    }
}
