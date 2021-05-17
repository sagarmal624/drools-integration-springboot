package com.example.demo.service;

import com.example.demo.Pipeline;
import com.example.demo.domain.APIDto;
import com.example.demo.domain.ApiResponse;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SwaggerService {
    @Autowired
    private KieContainer kieContainer;

    public static void main(String[] args) {
        isNounOrProun("save");
    }
//    public static void main(String[] args) {
//        SwaggerService swagger = new SwaggerService();
//        List<APIDto> apiDtos = swagger.extractAPIInformation("https://petstore3.swagger.io/api/v3/openapi.json");
//    }

    public List<ApiResponse> getRules(String swaggerUrl) {
        List<APIDto> apiDtos = extractAPIInformation(swaggerUrl);
        return apiDtos.stream().map(it -> {
            return ApiResponse.builder().url(it.getUrl()).requestType(it.getType()).messages(it.getMessages()).score(it.getScore()).build();
        }).collect(Collectors.toList());
    }

    public List<APIDto> extractAPIInformation(String swaggerUrl) {
        RestTemplate restTemplate = new RestTemplate();
        List<APIDto> apiDtos = new ArrayList<>();
        Map<String, Object> response = restTemplate.getForEntity(swaggerUrl, HashMap.class).getBody();
        Map<String, Object> paths = (Map<String, Object>) response.get("paths");
        for (Map.Entry<String, Object> entry : paths.entrySet()) {
            Map<String, Object> apiTypes = (Map<String, Object>) entry.getValue();
            for (Map.Entry<String, Object> api : apiTypes.entrySet()) {
                APIDto apiDto = APIDto.builder()
                        .type(api.getKey())
                        .url(entry.getKey())
                        .responseCodes(getResponseCode((Map<String, Object>) api.getValue()))
                        .build();
                KieSession kieSession = kieContainer.newKieSession();
                kieSession.insert(apiDto);
                kieSession.fireAllRules();
                kieSession.dispose();
                apiDtos.add(apiDto);
            }
        }
        return apiDtos;
    }

    private static void isNounOrProun(String url) {
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(url);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        for (CoreLabel coreLabel : coreLabels) {
            String pos = coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            System.out.println(coreLabel.originalText() + "=" + pos);
        }
    }

    private List<String> getResponseCode(Map<String, Object> map) {
        Map<String, Object> responses = (Map<String, Object>) map.get("responses");
        return responses.entrySet().stream().map(it -> it.getKey()).collect(Collectors.toList());
    }
}
