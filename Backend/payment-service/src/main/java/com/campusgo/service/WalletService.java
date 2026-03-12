package com.campusgo.service;

import com.campusgo.dto.WalletAccountDTO;
import com.campusgo.dto.WalletOrderPaymentDTO;
import com.campusgo.dto.WalletPayOrderRequest;
import com.campusgo.dto.WalletSettleRequest;
import com.campusgo.dto.WalletTransactionDTO;
import com.campusgo.enums.WalletOwnerType;

import java.util.List;

public interface WalletService {
    WalletAccountDTO topup(WalletOwnerType ownerType, Long ownerId, Long amountCents, String idempotencyKey, String remark);
    WalletAccountDTO getWallet(WalletOwnerType ownerType, Long ownerId);
    List<WalletTransactionDTO> listTransactions(WalletOwnerType ownerType, Long ownerId, Integer limit);
    WalletOrderPaymentDTO payOrder(WalletPayOrderRequest req);
    WalletOrderPaymentDTO settle(WalletSettleRequest req);
}
