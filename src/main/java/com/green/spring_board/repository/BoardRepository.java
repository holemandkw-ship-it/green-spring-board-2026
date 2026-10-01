package com.green.spring_board.repository;

import com.green.spring_board.entity.Boards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Boards,Integer> {
    List<Boards> id(int id);
}
