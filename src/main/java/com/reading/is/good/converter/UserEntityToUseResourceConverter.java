package com.reading.is.good.converter;

import com.reading.is.good.entity.UserEntity;
import com.reading.is.good.resource.UserResource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUseResourceConverter implements Converter<UserEntity, UserResource> {

    @Override
    public UserResource convert(UserEntity userEntity) {
        UserResource userResource = new UserResource();
        userResource.setName(userEntity.getName());
        userResource.setUserName(userEntity.getUserName());
        return userResource;
    }
}
