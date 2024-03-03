package com.openai.gpt.presentation;

import com.openai.gpt.presentation.dto.CompletionRequestDto;
import com.openai.gpt.service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/chatGpt")
@RequiredArgsConstructor
public class ChatGPTController {
  private final ChatGPTService chatGPTService;

  /**
   * [API] ChatGPT 모델 리스트를 조회합니다
   *
   * @return
   */

  @GetMapping("/modelList")
  public ResponseEntity<List<Map<String, Object>>> selectModelList() {
    List<Map<String, Object>> result = chatGPTService.modelList();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * [API] ChatGPT 유효한 모델인지 조회합니다
   *
   * @param modelName
   * @return
   */

  @GetMapping("/model")
  public ResponseEntity<Map<String, Object>> isValidModel(@RequestParam(name = "modelName") String modelName) {
    Map<String, Object> result = chatGPTService.isValidModel(modelName);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  /**
   * [API] ChatGPT 모델리스트를 조회합니다
   *
   * @param completionRequestDto
   * @return
   */

  @PostMapping("/prompt")
  public ResponseEntity<Map<String, Object>> selectPrompt(@RequestBody CompletionRequestDto completionRequestDto) {
    Map<String, Object> result = chatGPTService.prompt(completionRequestDto);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
