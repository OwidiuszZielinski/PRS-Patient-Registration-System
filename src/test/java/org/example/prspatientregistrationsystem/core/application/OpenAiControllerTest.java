package org.example.prspatientregistrationsystem.core.application;

import org.example.prspatientregistrationsystem.openai.OpenAiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAiControllerTest {

    @Mock
    private OpenAiService openAiService;

    @InjectMocks
    private OpenAiController openAiController;

    @Test
    void getOpenAi_ShouldReturnResponseFromService() {
        int temperature = 5;
        String expectedResponse = "Recommended warm clothes for temperature 5 degrees.";

        when(openAiService.getClothesInfo(temperature)).thenReturn(expectedResponse);

        String actualResponse = openAiController.getOpenAi(temperature);

        assertEquals(expectedResponse, actualResponse);
        verify(openAiService, times(1)).getClothesInfo(temperature);
    }
}