package com.cognify.model.dto;

public class QuestionResponse {
    private Long id;
    private String text;
    private String type;
    private Integer displayOrder;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String text, String type, Integer displayOrder) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}