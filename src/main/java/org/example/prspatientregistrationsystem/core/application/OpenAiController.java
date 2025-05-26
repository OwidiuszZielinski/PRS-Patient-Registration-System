package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.example.prspatientregistrationsystem.openai.OpenAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping(path = "/{temperature}")
    public String getOpenAi(@PathVariable Integer temperature) {
        return openAiService.getClothesInfo(temperature);
    }
}
