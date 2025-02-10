package com.example.board.repository;

import com.example.board.entity.SharePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SharePostRepository extends JpaRepository<SharePost, Long> {
    List<SharePost> findByTxnBoardTitleContaining(String keyword);
}
