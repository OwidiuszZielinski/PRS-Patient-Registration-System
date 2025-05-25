package org.example.prspatientregistrationsystem.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatModel chatModel;

    public String getClothesInfo(Integer temperature) {
        var prompt = """
        When the temperature is %d°C, please give me a **one-sentence** tip on what to wear.
        """.formatted(temperature);
        return chatModel.call(new UserMessage(prompt));
    }

}
