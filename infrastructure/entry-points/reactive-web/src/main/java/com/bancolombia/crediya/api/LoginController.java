package com.bancolombia.crediya.api;

import com.bancolombia.crediya.api.dto.LoginRequest;
import com.bancolombia.crediya.api.dto.LoginResponse;
import com.bancolombia.crediya.usecase.login.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/api/v1/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
        return loginUseCase.login(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.ok(new LoginResponse(token)));
    }
    
}
