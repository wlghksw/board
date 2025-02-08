package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TXN_Posts")
public class SharePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnPostId;

    @Column(nullable = false, length = 200)
    private String txnBoardTitle;

    @Column(nullable = false, length = 255)
    private String txnBoardContent;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 255)
    private String userId;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false, length = 255)
    private String authorName;

    @Column(nullable = false)
    private LocalDateTime postDate;

    @Column(nullable = false)
    private Integer viewCount;

    @Column(nullable = true, length = 500)
    private String photoUrl;
}
