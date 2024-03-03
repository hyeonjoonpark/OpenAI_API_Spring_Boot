package com.openai.gpt.service.implement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.gpt.config.ChatGPTConfig;
import com.openai.gpt.presentation.dto.CompletionRequestDto;
import com.openai.gpt.service.ChatGPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTServiceImpl implements ChatGPTService {
  private final ChatGPTConfig gptConfig;

  @Value("${openai.model}")
  private String model;

  /**
   * 사용 가능한 모델 리스트를 조회하는 비즈니스 로직
   *
   * @return
   */

  @Override
  public List<Map<String, Object>> modelList() {
    log.debug("[+] 모델 리스트를 조회합니다");
    List<Map<String, Object>> resultList = null;

    // [STEP1] 토큰 정보가 포함된 Header를 가져옵니다.
    HttpHeaders headers = gptConfig.httpHeaders();

    // [STEP2] 통신을 위한 RestTemplate을 구성합니다.

    ResponseEntity<String> response = gptConfig.restTemplate()
      .exchange(
        "https://api.openai.com/v1/models",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        String.class
      );

    try {
      // [STEP3] Jackson을 기반으로 응답값을 가져옵니다.
      ObjectMapper om = new ObjectMapper();
      Map<String, Object> data =
        om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
        });
      resultList = (List<Map<String, Object>>) data.get("data");

      // [STEP4] 응답 값을 결과값에 넣고 출력을 해봅니다.
      for (Map<String, Object> object : resultList) {
        log.debug("ID: " + object.get("id"));
        log.debug("Object: " + object.get("object"));
        log.debug("Created: " + object.get("created"));
        log.debug("Owned By: " + object.get("owned_by"));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return resultList;
  }

  /**
   * ChatGTP 프롬프트 검색
   * TODO : 에러 고치기
   *
   * @param completionRequestDto
   * @return
   */

  @Override
  public Map<String, Object> prompt(CompletionRequestDto completionRequestDto) {
    log.debug("[+] 프롬프트를 수행합니다");
    Map<String, Object> result = new HashMap<>();

    // [STEP1] 토큰 정보가 포함된 Header를 가져옵니다.
    HttpHeaders headers = gptConfig.httpHeaders();

    String requestBody = "";
    ObjectMapper om = new ObjectMapper();

    // [STEP2] properties의 model을 가져와서 객체에 추가합니다.
    completionRequestDto = CompletionRequestDto.builder()
      .model(model)
      .prompt(completionRequestDto.getPrompt())
      .temperature(0.8f)
      .build();

    try {
      // [STEP3] Object -> String 직렬화를 구성합니다.
      requestBody = om.writeValueAsString(completionRequestDto);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // [STEP4] 통신을 위한 RestTemplate을 구성합니다.
    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> response = gptConfig.restTemplate()
      .exchange(
        "https://api.openai.com/v1/completions",
        HttpMethod.POST,
        requestEntity,
        String.class
      );

    try {
      // [STEP6] String -> HashMap 역직렬화를 구성합니다.
      result = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  /**
   * 모델이 유효한지 확인하는 비즈니스 로직
   *
   * @param modelName
   * @return
   */

  @Override
  public Map<String, Object> isValidModel(String modelName) {
    log.debug("[+] 모델이 유효한지 조회합니다.");
    log.debug("모델 : " + modelName);
    Map<String, Object> result;

    // [STEP1] 토큰 정보가 포함된 Header를 가져옵니다.
    HttpHeaders headers = gptConfig.httpHeaders();

    // [STEP2] 통신을 위한 RestTemplate을 구성합니다.
    ResponseEntity<String> response = gptConfig.restTemplate()
      .exchange(
        "https://api.openai.com/v1/models/" + modelName,
        HttpMethod.GET,
        new HttpEntity<>(headers),
        String.class
      );

    try {
      // [STEP3] Jackson을 기반으로 응답값을 가져옵니다.
      ObjectMapper om = new ObjectMapper();
      result = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
