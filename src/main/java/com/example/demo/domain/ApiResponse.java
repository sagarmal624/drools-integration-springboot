package com.example.demo.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiResponse {
    private String requestType;
    private String url;
    private Integer score;
    private List<String> messages;
}
