package epotters.ai.poem;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class PoemController {

  private static final String LLM_COMMUNICATION_ERROR =
      "Unable to communicate with the configured LLM. Please try again later.";

  private final PoemGenerationService poemGenerationService;

  public PoemController(PoemGenerationService poemGenerationService) {
    this.poemGenerationService = poemGenerationService;
  }

  @PostMapping("/poems")
  ResponseEntity<Poem> generate(@RequestBody PoemGenerationRequest request) {
    Poem response = poemGenerationService.generate(request.genre, request.theme);
    return ResponseEntity.ok(response);
  }

  record PoemGenerationRequest(String genre, String theme) {
  }

  @ExceptionHandler(OpenAiApiClientErrorException.class)
  ProblemDetail handle(OpenAiApiClientErrorException exception) {
    log.error("OpenAI returned an error.", exception);
    return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, LLM_COMMUNICATION_ERROR);
  }
}
