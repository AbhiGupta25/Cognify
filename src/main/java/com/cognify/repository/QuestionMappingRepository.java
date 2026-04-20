package com.cognify.repository;

import com.cognify.model.entity.Question;
import com.cognify.model.entity.QuestionMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionMappingRepository extends JpaRepository<QuestionMapping, Long> {
    List<QuestionMapping> findByQuestion(Question question);
}