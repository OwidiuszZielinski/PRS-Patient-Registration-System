package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.openai.OpenAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping(path = "/{temperature}")
    public String getOpenAi(@PathVariable Integer temperature) {
        return openAiService.getClothesInfo(temperature);
    }
}
