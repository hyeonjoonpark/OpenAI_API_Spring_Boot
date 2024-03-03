package com.openai.gpt.service;

import com.openai.gpt.presentation.dto.CompletionRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ChatGPT Service interface
 */

@Service
public interface ChatGPTService {
  List<Map<String, Object>> modelList();
  Map<String, Object> prompt(CompletionRequestDto completionRequestDto);
  Map<String, Object> isValidModel(String modelName);
}
