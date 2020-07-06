package com.nthieu.productservice.aop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerActivity {
    private String username;
    private LocalDateTime actionOn;
    private String viewProductId;
    private Map<String, List<String>> filterBy;
    private List<String> sortBy;

    public CustomerActivity(String username, LocalDateTime actionOn, String viewProductId) {
        this.username = username;
        this.actionOn = actionOn;
        this.viewProductId = viewProductId;
    }
}
