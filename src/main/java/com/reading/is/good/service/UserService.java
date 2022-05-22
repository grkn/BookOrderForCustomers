package com.reading.is.good.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.entity.AuthorizationEntity;
import com.reading.is.good.entity.UserEntity;
import com.reading.is.good.exception.BadRequestException;
import com.reading.is.good.exception.NotFoundException;
import com.reading.is.good.model.Customer;
import com.reading.is.good.model.Order;
import com.reading.is.good.mongorepos.CustomerRepository;
import com.reading.is.good.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserEntity findUserByName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException(String.format("User can not be found by given username : %s",
                        userName)));
    }

    @Transactional
    public UserEntity createUserEntity(UserEntity userEntity) {
        LOGGER.trace("Create user request is received for user: {} ", userEntity.getUserName());
        if (userRepository.findByUserName(userEntity.getUserName()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        authorizationEntity.setAuth(ChallengeConstant.ROLE_USER);
        authorizationEntity.setUsers(Set.of(userEntity));

        userEntity.setAuthorizations(Set.of(authorizationEntity));
        String password = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(password);


        userEntity = userRepository.save(userEntity);

        customerRepository.save(Customer.builder()
                .username(userEntity.getUserName())
                .build());
        LOGGER.trace("Create user request is finished for user: {} ", userEntity.getUserName());
        return userEntity;
    }

    public String createToken(UserEntity userEntity) {
        LOGGER.trace("Create access token request is received for user: {} ", userEntity.getUserName());

        UserEntity persistedUser = findUserByName(userEntity.getUserName());

        if (passwordEncoder.matches(userEntity.getPassword(), persistedUser.getPassword())) {
            LOGGER.trace("Create access token request is finished for user: {} ", userEntity.getUserName());
            return JWT.create()
                    .withSubject(persistedUser.getUserName())
                    .withExpiresAt(new Date(System.currentTimeMillis() + ChallengeConstant.EXPIRE_TIME))
                    .sign(Algorithm.HMAC512(ChallengeConstant.DUMMY_SIGN.getBytes()));
        }

        throw new NotFoundException("Username and password are not valid");
    }

    public Page<Order> findOrdersByCustomer(String username, Pageable pageable) {
        AggregationOperation match = Aggregation.match(Criteria.where("orders").exists(true));
        AggregationOperation customerName = Aggregation.match(Criteria.where("username").is(username));
        AggregationOperation unwind = Aggregation.unwind("orders");
        AggregationOperation skipOperation = Aggregation.skip(pageable.getOffset());
        AggregationOperation limitOperation = Aggregation.limit(pageable.getPageSize());
        AggregationOperation projection = Aggregation.project("orders.orderId", "orders.status", "orders.orderDate",
                "orders.orderDetail", "orders.totalPrice");
        Aggregation aggregation = Aggregation.newAggregation(match, customerName, unwind, projection, skipOperation, limitOperation);

        AggregationResults<Order> orders = mongoTemplate
                .aggregate(aggregation, mongoTemplate.getCollectionName(Customer.class), Order.class);
        return new PageImpl<>(orders.getMappedResults(), pageable, orders.getMappedResults().size());
    }
}
