package com.cognify.controller;

import com.cognify.model.dto.AssessmentResultResponse;
import com.cognify.model.dto.ComparisonResultResponse;
import com.cognify.model.dto.QuestionResponse;
import com.cognify.model.dto.SubmitAssessmentRequest;
import com.cognify.service.AssessmentService;
import com.cognify.service.ComparisonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "*")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final ComparisonService comparisonService;

    public AssessmentController(AssessmentService assessmentService,
                                ComparisonService comparisonService) {
        this.assessmentService = assessmentService;
        this.comparisonService = comparisonService;
    }

    @GetMapping("/questions")
    public List<QuestionResponse> getAllQuestions() {
        return assessmentService.getAllQuestions();
    }

    @PostMapping("/submit")
    public AssessmentResultResponse submitAssessment(@RequestBody SubmitAssessmentRequest request) {
        return assessmentService.submitAssessment(request);
    }

    @GetMapping("/{attemptId}")
    public AssessmentResultResponse getAttemptResult(@PathVariable Long attemptId,
                                                     @RequestParam(required = false) Long userId) {
        return assessmentService.getAttemptResult(attemptId, userId);
    }

    @GetMapping("/compare")
    public ComparisonResultResponse compareAttempts(@RequestParam Long oldAttemptId,
                                                    @RequestParam Long newAttemptId) {
        return comparisonService.compareAttempts(oldAttemptId, newAttemptId);
    }
}
