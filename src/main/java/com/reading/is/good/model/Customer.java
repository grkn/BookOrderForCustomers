package com.reading.is.good.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("readingIsGood")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Customer extends AuditMetadata {

    @Id
    private String id;
    private String name;
    private String username;
    private List<Order> orders;
    @Version
    private Long version;
}
