package com.reading.is.good.controller;


import com.reading.is.good.dto.UserDto;
import com.reading.is.good.entity.UserEntity;
import com.reading.is.good.resource.TokenResource;
import com.reading.is.good.resource.UserResource;
import com.reading.is.good.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;

public class AuthControllerTest {

    private static final String ANY_NAME = "anyName";
    private static final String ANY_PASSWORD = "anyPassword";
    private static final String ANY_USER_NAME = "anyUserName";
    private static final String ANY_TOKEN = "anyToken";

    private AuthController authController;

    @Mock
    private ConversionService conversionService;

    @Mock
    private UserService userService;

    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(conversionService, userService);

        userDto = UserDto.builder()
                .name(ANY_NAME)
                .userName(ANY_USER_NAME)
                .password(ANY_PASSWORD)
                .build();

        userEntity = UserEntity.builder()
                .userName(ANY_NAME)
                .password(ANY_PASSWORD)
                .build();
    }

    @Nested
    class LoginEndpoint {
        @Test
        void givenValidUserDto_whenAuthorizeUser_thenUserSignedUpSuccessfully() {
            // Given
            UserResource userResource = UserResource.builder()
                    .name(ANY_NAME)
                    .userName(ANY_USER_NAME)
                    .build();

            Mockito.when(conversionService.convert(userDto, UserEntity.class)).thenReturn(userEntity);
            Mockito.when(userService.createUserEntity(userEntity)).thenReturn(userEntity);
            Mockito.when(conversionService.convert(userEntity, UserResource.class)).thenReturn(userResource);
            // When
            ResponseEntity<UserResource> userResourceResponseEntity = authController.authorize(userDto);
            // Then
            Assertions.assertThat(userResourceResponseEntity.getBody()).isNotNull();
            Assertions.assertThat(userResourceResponseEntity.getBody().getName()).isEqualTo(ANY_NAME);
            Assertions.assertThat(userResourceResponseEntity.getBody().getUserName()).isEqualTo(ANY_USER_NAME);
        }
    }

    @Nested
    class TokenEndpoint {
        @Test
        void givenValidUserDto_whenGetAccessToken_thenUserGetsTokenSuccessfully() {
            // Given
            Mockito.when(conversionService.convert(userDto, UserEntity.class)).thenReturn(userEntity);
            Mockito.when(userService.createToken(userEntity)).thenReturn(ANY_TOKEN);

            // When
            ResponseEntity<TokenResource> tokenResourceResponseEntity = authController.getAccessToken(userDto);

            // Then
            Assertions.assertThat(tokenResourceResponseEntity.getBody()).isNotNull();
            Assertions.assertThat(tokenResourceResponseEntity.getBody().getAccessToken()).isEqualTo(ANY_TOKEN);
        }
    }
}
