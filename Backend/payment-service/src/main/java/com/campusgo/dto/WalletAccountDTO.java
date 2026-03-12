package com.campusgo.dto;

import com.campusgo.enums.WalletOwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountDTO {
    private WalletOwnerType ownerType;
    private Long ownerId;
    private Long balanceCents;
    private Long frozenCents;
}
