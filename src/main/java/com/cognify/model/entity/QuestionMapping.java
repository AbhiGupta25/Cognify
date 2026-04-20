package com.cognify.model.entity;

import com.cognify.model.enumtype.MappingTargetType;
import jakarta.persistence.*;

@Entity
@Table(name = "question_mappings")
public class QuestionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MappingTargetType targetType;

    @Column(nullable = false)
    private String targetKey;

    @Column(nullable = false)
    private Integer weight;

    @Column(nullable = false)
    private Integer direction;

    public QuestionMapping() {
    }

    public QuestionMapping(Question question, MappingTargetType targetType, String targetKey, Integer weight, Integer direction) {
        this.question = question;
        this.targetType = targetType;
        this.targetKey = targetKey;
        this.weight = weight;
        this.direction = direction;
    }

    public Long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }

    public MappingTargetType getTargetType() {
        return targetType;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setTargetType(MappingTargetType targetType) {
        this.targetType = targetType;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }
}