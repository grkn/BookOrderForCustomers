package com.reading.is.good.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Min;
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
public class BookPatchDto {

    @Min(1)
    private Integer quantity;

    private String name;

    @Min(0)
    private BigDecimal price;
}
