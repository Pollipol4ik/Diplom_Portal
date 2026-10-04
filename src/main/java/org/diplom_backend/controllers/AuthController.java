package org.diplom_backend.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.diplom_backend.dto.requests.AccountLoginRequestDto;
import org.diplom_backend.dto.requests.AccountRegistrationRequestDto;
import org.diplom_backend.dto.responses.AuthResponseDto;
import org.diplom_backend.exceptions.RoleNotFoundException;
import org.diplom_backend.mappers.AccountMapper;
import org.diplom_backend.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Работа с авторизацией")
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final AccountMapper accountMapper;

    @PostMapping("/register")
    @Operation(description = "Зарегистрировать нового пользователя", summary = "Зарегистрировать нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно зарегистрирован новый пользователь"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public AuthResponseDto registerUserAccount(@RequestBody @Valid AccountRegistrationRequestDto request, HttpServletResponse response) throws RoleNotFoundException {
        var answer = authService.register(request.email().trim(), request.nickname().trim(), request.password().trim());
        addCookie(answer.getSecond(), response);
        return new AuthResponseDto(answer.getFirst().getNickname(), answer.getFirst().getRole().getName().getDescription());
    }

    @PostMapping("/login")
    @Operation(description = "Войти на сайт", summary = "Войти на сайт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно вошел на сайт"),
            @ApiResponse(responseCode = "400", description = "Что-то пошло не так")
    })
    public AuthResponseDto loginUserAccount(@RequestBody @Valid AccountLoginRequestDto request, HttpServletResponse response) {
        var answer = authService.login(request.email().trim(), request.password().trim());
        addCookie(answer.getSecond(), response);
        return new AuthResponseDto(answer.getFirst().getNickname(), answer.getFirst().getRole().getName().getDescription());
    }

    private void addCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

}
