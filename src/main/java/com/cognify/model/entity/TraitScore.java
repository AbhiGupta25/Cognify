package com.cognify.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trait_scores")
public class TraitScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id")
    private AssessmentAttempt attempt;

    @Column(nullable = false)
    private String traitName;

    @Column(nullable = false)
    private Integer score;

    public TraitScore() {
    }

    public TraitScore(AssessmentAttempt attempt, String traitName, Integer score) {
        this.attempt = attempt;
        this.traitName = traitName;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public AssessmentAttempt getAttempt() {
        return attempt;
    }

    public String getTraitName() {
        return traitName;
    }

    public Integer getScore() {
        return score;
    }

    public void setAttempt(AssessmentAttempt attempt) {
        this.attempt = attempt;
    }

    public void setTraitName(String traitName) {
        this.traitName = traitName;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}