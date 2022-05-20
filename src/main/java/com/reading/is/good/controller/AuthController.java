package com.reading.is.good.controller;

import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.dto.UserDto;
import com.reading.is.good.entity.UserEntity;
import com.reading.is.good.resource.TokenResource;
import com.reading.is.good.resource.UserResource;
import com.reading.is.good.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = ChallengeConstant.BASE_URL)
@AllArgsConstructor
public class AuthController {
    private final ConversionService conversionService;
    private final UserService userService;

    @PostMapping("/authorize")
    public ResponseEntity<UserResource> authorize(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(conversionService.convert(userService.createUserEntity(conversionService.convert(userDto,
                UserEntity.class)), UserResource.class));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResource> getAccessToken(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(new TokenResource(userService.
                createToken(conversionService.convert(userDto, UserEntity.class))));
    }
}
