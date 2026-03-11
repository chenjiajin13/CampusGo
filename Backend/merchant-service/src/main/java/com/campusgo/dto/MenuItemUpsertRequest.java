package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemUpsertRequest {
    private String name;
    private Long priceCents;
    private Boolean enabled;
}

