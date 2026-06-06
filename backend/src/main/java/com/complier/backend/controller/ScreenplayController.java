package com.complier.backend.controller;

import com.complier.backend.dto.ConvertRequest;
import com.complier.backend.dto.ConvertResponse;
import com.complier.backend.entity.PromptTemplate;
import com.complier.backend.entity.ScreenplayHistory;
import com.complier.backend.registry.PromptRegistry;
import com.complier.backend.repository.ScreenplayHistoryRepository;
import com.complier.backend.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/screenplay")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequiredArgsConstructor
public class ScreenplayController {

    private final AiService aiService;
    private final PromptRegistry promptRegistry;
    private final ScreenplayHistoryRepository historyRepository;

    /**
     * Get all predefined prompt styles.
     */
    @GetMapping("/prompts")
    public ResponseEntity<List<PromptTemplate>> getPrompts() {
        return ResponseEntity.ok(promptRegistry.getAllTemplates());
    }

    /**
     * Convert novel text to screenplay using chosen style and LLM API.
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convert(@RequestBody ConvertRequest request) {
        log.info("Received screenplay conversion request for title: {}", request.getTitle());
        
        if (request.getNovelText() == null || request.getNovelText().trim().isBlank()) {
            return ResponseEntity.badRequest().body("Novel text cannot be empty.");
        }

        // Get system prompt based on style
        String styleId = request.getPromptStyle();
        if (styleId == null || styleId.trim().isBlank()) {
            styleId = "standard";
        }

        PromptTemplate template = promptRegistry.getTemplateById(styleId);
        if (template == null) {
            log.warn("Requested style '{}' not found, falling back to 'standard'", styleId);
            template = promptRegistry.getTemplateById("standard");
        }

        try {
            // Call AI Service
            String screenplay = aiService.generateScreenplay(
                    template.getSystemPrompt(),
                    request.getNovelText(),
                    request.getApiUrl(),
                    request.getApiKey(),
                    request.getModel()
            );

            String activeModel = aiService.getActiveModel(request.getModel());

            // Save to database
            ScreenplayHistory history = ScreenplayHistory.builder()
                    .title(request.getTitle() != null && !request.getTitle().isBlank() ? request.getTitle() : "未命名片段")
                    .novelText(request.getNovelText())
                    .screenplayText(screenplay)
                    .promptStyle(template.getName())
                    .modelUsed(activeModel)
                    .build();

            ScreenplayHistory saved = historyRepository.save(history);

            ConvertResponse response = ConvertResponse.builder()
                    .screenplayText(screenplay)
                    .historyId(saved.getId())
                    .modelUsed(activeModel)
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request parameters: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error converting screenplay: ", e);
            return ResponseEntity.internalServerError().body("Conversion failed: " + e.getMessage());
        }
    }

    /**
     * Get conversion history list.
     */
    @GetMapping("/history")
    public ResponseEntity<List<ScreenplayHistory>> getHistory() {
        List<ScreenplayHistory> histories = historyRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(histories);
    }

    /**
     * Get specific conversion history record by ID.
     */
    @GetMapping("/history/{id}")
    public ResponseEntity<ScreenplayHistory> getHistoryById(@PathVariable Long id) {
        Optional<ScreenplayHistory> history = historyRepository.findById(id);
        return history.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete a specific conversion history record.
     */
    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        if (historyRepository.existsById(id)) {
            historyRepository.deleteById(id);
            log.info("Deleted history record with ID: {}", id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Clear all conversion history.
     */
    @DeleteMapping("/history/clear")
    public ResponseEntity<Void> clearHistory() {
        historyRepository.deleteAll();
        log.info("Cleared all history records.");
        return ResponseEntity.ok().build();
    }
}
