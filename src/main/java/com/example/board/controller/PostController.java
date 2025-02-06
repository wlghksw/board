package com.example.board.controller;

import com.example.board.entity.Post;
import com.example.board.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/board")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // ✅ 기본 페이지 (방 있음 목록으로 이동)
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

    // 방없음
    @GetMapping("/no-rooms")
    public String listNoRooms(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postsPage = postService.getPostsByCategory("방 없음", pageable);

        model.addAttribute("postsPage", postsPage);
        return "noRoomList";
    }

    // 글 작성 페이지 이동
    @GetMapping("/post/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "postWrite";
    }

    // 글 저장
    @PostMapping("/post/save")
    public String savePost(@RequestParam("rm_board_title") String rmBoardTitle,
                           @RequestParam("post_content") String postContent,
                           @RequestParam("amount") Integer amount,
                           @RequestParam("deposit") Integer deposit,
                           @RequestParam("author_region") String authorRegion,
                           @RequestParam("category") String category,
                           @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {

        Post post = new Post();
        post.setRm_board_title(rmBoardTitle);
        post.setPost_content(postContent);
        post.setAmount(amount != null ? amount : 0);
        post.setDeposit(deposit != null ? deposit : 0);
        post.setAuthor_region(authorRegion);
        post.setCategory(category);
        post.setPostDate(LocalDateTime.now());
        post.setPost_views(0);

        // 로그인 기능이 없으므로 임시 기본 값 적용
        post.setAuthor_age(25); // 테스트용 기본값
        post.setAuthor_name("테스트 사용자");
        post.setAuthor_gender("M");
        post.setUser_id("testUser"); // 임시 ID
        post.setUser_preference("default"); // 기본 값

        // 파일 업로드 처리 수정 필요 적용 안됨
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String uploadDir = "C:/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String originalFilename = photoFile.getOriginalFilename();
                String filePath = uploadDir + originalFilename;
                photoFile.transferTo(new File(filePath));

                post.setPhotoUrl("/uploads/" + originalFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 데이터베이스에 저장
        postService.savePost(post);

        return category.equals("방 있음") ? "redirect:/board/rooms" : "redirect:/board/no-rooms";
    }
}
