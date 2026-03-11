package com.campusgo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private Long id;
    private Long merchantId;
    private String name;
    private Long priceCents;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}

