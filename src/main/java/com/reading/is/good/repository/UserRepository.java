package com.reading.is.good.repository;

import com.reading.is.good.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findByUserName(String userName);
}
