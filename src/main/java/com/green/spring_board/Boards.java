package com.green.spring_board;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class Boards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    // 기본 생성자
    public Boards() {
    }

    // 생성자
    public Boards(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}