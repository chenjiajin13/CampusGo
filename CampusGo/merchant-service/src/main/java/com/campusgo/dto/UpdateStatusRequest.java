package com.campusgo.dto;


import com.campusgo.enums.MerchantStatus;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    private MerchantStatus status;
}
