package com.nthieu.auditservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CustomerActivity {
    @Id
    private String id;
    private String username;
    private LocalDateTime actionOn;
    private String viewProductId;
    private Map<String, List<String>> filterBy;
    private List<String> sortBy;
}
