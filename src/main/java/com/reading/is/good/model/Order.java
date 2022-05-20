package com.reading.is.good.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.reading.is.good.serialization.MoneySerializer;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Order {

    private String orderId;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime orderDate;
    private String description;
    private String status;
    @JsonSerialize(using = MoneySerializer.class)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalPrice;
    @Valid
    @NotNull
    private Detail orderDetail;
}
