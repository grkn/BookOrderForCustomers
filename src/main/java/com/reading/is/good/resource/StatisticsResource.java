package com.reading.is.good.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.reading.is.good.serialization.MoneySerializer;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class StatisticsResource {

    private String month;
    private int totalBookCount;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalPrice;
    private int totalOrderCount;
}
