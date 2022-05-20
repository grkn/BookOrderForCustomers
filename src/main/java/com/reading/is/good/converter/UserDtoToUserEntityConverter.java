package com.reading.is.good.converter;

import com.reading.is.good.dto.UserDto;
import com.reading.is.good.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserEntityConverter implements Converter<UserDto, UserEntity> {
    @Override
    public UserEntity convert(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userDto.getName());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setUserName(userDto.getUserName());
        return userEntity;
    }
}
