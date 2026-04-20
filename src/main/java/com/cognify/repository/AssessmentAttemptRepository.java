package com.cognify.repository;

import com.cognify.model.entity.AssessmentAttempt;
import com.cognify.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {
    List<AssessmentAttempt> findByUserOrderByAttemptDateDesc(User user);
}