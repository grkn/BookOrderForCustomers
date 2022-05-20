package com.reading.is.good.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.reading.is.good.serialization.MoneySerializer;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {

    @NotBlank
    private String bookId;

    @NotBlank
    private String name;

    @Min(0)
    @NotNull
    @JsonSerialize(using = MoneySerializer.class)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;
}
