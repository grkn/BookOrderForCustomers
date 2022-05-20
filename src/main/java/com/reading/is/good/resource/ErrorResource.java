package com.reading.is.good.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorResource {

    private String code;
    private String reasonMessage;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime createdTime = LocalDateTime.now();

    public ErrorResource(String code, String reasonMessage) {
        this.code = code;
        this.reasonMessage = reasonMessage;
    }
}
