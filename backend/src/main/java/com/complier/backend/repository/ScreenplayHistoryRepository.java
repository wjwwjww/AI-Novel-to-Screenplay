package com.complier.backend.repository;

import com.complier.backend.entity.ScreenplayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenplayHistoryRepository extends JpaRepository<ScreenplayHistory, Long> {
    List<ScreenplayHistory> findAllByOrderByCreatedAtDesc();
}
