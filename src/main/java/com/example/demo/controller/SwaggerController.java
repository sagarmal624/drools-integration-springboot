package com.example.demo.controller;

import com.example.demo.domain.ApiResponse;
import com.example.demo.service.SwaggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/swagger/api")
@RestController
public class SwaggerController {
    @Autowired
    private SwaggerService swaggerService;

    @GetMapping("/validate")
    public List<ApiResponse> validate() {
        return swaggerService.getRules("https://petstore3.swagger.io/api/v3/openapi.json");
    }
}
