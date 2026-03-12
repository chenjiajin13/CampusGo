package com.campusgo.domain;

import com.campusgo.enums.WalletOwnerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccount {
    private Long id;
    private WalletOwnerType ownerType;
    private Long ownerId;
    private Long balanceCents;
    private Long frozenCents;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
