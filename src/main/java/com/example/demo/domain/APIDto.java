package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class APIDto {
    private String url;
    private String type;
    private List<String> responseCodes;
    private List<String> securities;
    @Builder.Default
    private List<String> messages = new ArrayList<>();
    @Builder.Default
    private Integer score = 0;
}
