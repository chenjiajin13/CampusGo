package com.campusgo.service.impl;

import com.campusgo.domain.OrderPayment;
import com.campusgo.domain.OrderSettlement;
import com.campusgo.domain.Payment;
import com.campusgo.domain.WalletAccount;
import com.campusgo.domain.WalletTransaction;
import com.campusgo.dto.WalletAccountDTO;
import com.campusgo.dto.WalletOrderPaymentDTO;
import com.campusgo.dto.WalletPayOrderRequest;
import com.campusgo.dto.WalletSettleRequest;
import com.campusgo.dto.WalletTransactionDTO;
import com.campusgo.enums.OrderPaymentStatus;
import com.campusgo.enums.OrderSettlementStatus;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.enums.WalletBizType;
import com.campusgo.enums.WalletDirection;
import com.campusgo.enums.WalletOwnerType;
import com.campusgo.mapper.OrderPaymentMapper;
import com.campusgo.mapper.OrderSettlementMapper;
import com.campusgo.mapper.PaymentMapper;
import com.campusgo.mapper.WalletAccountMapper;
import com.campusgo.mapper.WalletTransactionMapper;
import com.campusgo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletAccountMapper walletAccountMapper;
    private final WalletTransactionMapper walletTransactionMapper;
    private final OrderPaymentMapper orderPaymentMapper;
    private final OrderSettlementMapper orderSettlementMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public WalletAccountDTO topup(WalletOwnerType ownerType, Long ownerId, Long amountCents, String idempotencyKey, String remark) {
        if (amountCents == null || amountCents <= 0) {
            throw new IllegalArgumentException("INVALID_TOPUP_AMOUNT");
        }
        WalletAccount account = ensureAccountForUpdate(ownerType, ownerId);
        long next = safeAdd(account.getBalanceCents(), amountCents);
        walletAccountMapper.updateBalance(account.getId(), next);
        walletTransactionMapper.insert(WalletTransaction.builder()
                .accountId(account.getId())
                .bizType(WalletBizType.TOPUP)
                .direction(WalletDirection.CREDIT)
                .amountCents(amountCents)
                .orderId(null)
                .idempotencyKey(idempotencyKey == null || idempotencyKey.isBlank() ? "topup:" + UUID.randomUUID() : idempotencyKey)
                .remark(remark == null ? "TOPUP" : remark)
                .createdAt(Instant.now())
                .build());
        WalletAccount latest = walletAccountMapper.findByOwner(ownerType, ownerId);
        return toAccountDTO(latest);
    }

    @Override
    public WalletAccountDTO getWallet(WalletOwnerType ownerType, Long ownerId) {
        WalletAccount account = walletAccountMapper.findByOwner(ownerType, ownerId);
        if (account == null) {
            return WalletAccountDTO.builder()
                    .ownerType(ownerType)
                    .ownerId(ownerId)
                    .balanceCents(0L)
                    .frozenCents(0L)
                    .build();
        }
        return toAccountDTO(account);
    }

    @Override
    public List<WalletTransactionDTO> listTransactions(WalletOwnerType ownerType, Long ownerId, Integer limit) {
        WalletAccount account = walletAccountMapper.findByOwner(ownerType, ownerId);
        if (account == null) {
            return new ArrayList<>();
        }
        int lim = (limit == null || limit <= 0 || limit > 200) ? 50 : limit;
        List<WalletTransaction> rows = walletTransactionMapper.listByAccountId(account.getId(), lim);
        List<WalletTransactionDTO> out = new ArrayList<>();
        if (rows != null) {
            for (WalletTransaction r : rows) {
                out.add(WalletTransactionDTO.builder()
                        .id(r.getId())
                        .bizType(r.getBizType())
                        .direction(r.getDirection())
                        .amountCents(r.getAmountCents())
                        .orderId(r.getOrderId())
                        .remark(r.getRemark())
                        .createdAt(r.getCreatedAt())
                        .build());
            }
        }
        return out;
    }

    @Override
    @Transactional
    public WalletOrderPaymentDTO payOrder(WalletPayOrderRequest req) {
        validatePayRequest(req);
        String idem = req.getIdempotencyKey() == null || req.getIdempotencyKey().isBlank()
                ? "pay:" + req.getOrderId()
                : req.getIdempotencyKey();

        OrderPayment byIdem = orderPaymentMapper.findByIdempotencyKey(idem);
        if (byIdem != null) {
            return toPaymentDTO(byIdem, null, null);
        }
        OrderPayment byOrder = orderPaymentMapper.findByOrderId(req.getOrderId());
        if (byOrder != null) {
            return toPaymentDTO(byOrder, null, null);
        }

        WalletAccount user = ensureAccountForUpdate(WalletOwnerType.USER, req.getUserId());
        if (user.getBalanceCents() == null || user.getBalanceCents() < req.getAmountCents()) {
            throw new IllegalArgumentException("WALLET_INSUFFICIENT_BALANCE");
        }
        long next = user.getBalanceCents() - req.getAmountCents();
        walletAccountMapper.updateBalance(user.getId(), next);
        walletTransactionMapper.insert(WalletTransaction.builder()
                .accountId(user.getId())
                .bizType(WalletBizType.PAY_ORDER)
                .direction(WalletDirection.DEBIT)
                .amountCents(req.getAmountCents())
                .orderId(req.getOrderId())
                .idempotencyKey(idem + ":debit")
                .remark("PAY_ORDER")
                .createdAt(Instant.now())
                .build());

        OrderPayment payment = OrderPayment.builder()
                .orderId(req.getOrderId())
                .userId(req.getUserId())
                .merchantId(req.getMerchantId())
                .runnerId(req.getRunnerId())
                .amountCents(req.getAmountCents())
                .status(OrderPaymentStatus.PAID_ESCROW)
                .idempotencyKey(idem)
                .paidAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        orderPaymentMapper.insert(payment);

        // Keep compatibility with existing payment query interface.
        Payment p = Payment.builder()
                .orderId(req.getOrderId())
                .userId(req.getUserId())
                .merchantId(req.getMerchantId())
                .amountCents(req.getAmountCents())
                .currency("SGD")
                .method(PaymentMethod.WALLET)
                .status(PaymentStatus.SUCCESS)
                .providerTxnId("WALLET-" + req.getOrderId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        paymentMapper.insert(p);

        return toPaymentDTO(payment, null, null);
    }

    @Override
    @Transactional
    public WalletOrderPaymentDTO settle(WalletSettleRequest req) {
        if (req == null || req.getOrderId() == null || req.getMerchantId() == null || req.getRunnerId() == null || req.getAmountCents() == null || req.getAmountCents() <= 0) {
            throw new IllegalArgumentException("INVALID_SETTLE_REQUEST");
        }
        String idem = req.getIdempotencyKey() == null || req.getIdempotencyKey().isBlank()
                ? "settle:" + req.getOrderId()
                : req.getIdempotencyKey();

        OrderSettlement byIdem = orderSettlementMapper.findByIdempotencyKey(idem);
        if (byIdem != null) {
            OrderPayment paid = orderPaymentMapper.findByOrderId(req.getOrderId());
            return toPaymentDTO(paid, byIdem.getMerchantCents(), byIdem.getRunnerCents());
        }

        OrderPayment paid = orderPaymentMapper.findByOrderId(req.getOrderId());
        if (paid == null) {
            throw new IllegalArgumentException("ORDER_NOT_PAID");
        }
        if (paid.getStatus() == OrderPaymentStatus.SETTLED) {
            OrderSettlement existing = orderSettlementMapper.findByOrderId(req.getOrderId());
            return toPaymentDTO(paid, existing == null ? null : existing.getMerchantCents(), existing == null ? null : existing.getRunnerCents());
        }
        if (paid.getStatus() != OrderPaymentStatus.PAID_ESCROW) {
            throw new IllegalArgumentException("ORDER_PAYMENT_STATUS_INVALID");
        }

        WalletAccount merchant = ensureAccountForUpdate(WalletOwnerType.MERCHANT, req.getMerchantId());
        WalletAccount runner = ensureAccountForUpdate(WalletOwnerType.RUNNER, req.getRunnerId());
        long gross = req.getAmountCents();
        long merchantCents = gross * 90 / 100;
        long runnerCents = gross - merchantCents;

        walletAccountMapper.updateBalance(merchant.getId(), safeAdd(merchant.getBalanceCents(), merchantCents));
        walletAccountMapper.updateBalance(runner.getId(), safeAdd(runner.getBalanceCents(), runnerCents));

        walletTransactionMapper.insert(WalletTransaction.builder()
                .accountId(merchant.getId())
                .bizType(WalletBizType.SETTLE_MERCHANT)
                .direction(WalletDirection.CREDIT)
                .amountCents(merchantCents)
                .orderId(req.getOrderId())
                .idempotencyKey(idem + ":merchant")
                .remark("SETTLE_90_PERCENT")
                .createdAt(Instant.now())
                .build());
        walletTransactionMapper.insert(WalletTransaction.builder()
                .accountId(runner.getId())
                .bizType(WalletBizType.SETTLE_RUNNER)
                .direction(WalletDirection.CREDIT)
                .amountCents(runnerCents)
                .orderId(req.getOrderId())
                .idempotencyKey(idem + ":runner")
                .remark("SETTLE_10_PERCENT")
                .createdAt(Instant.now())
                .build());

        orderSettlementMapper.insert(OrderSettlement.builder()
                .orderId(req.getOrderId())
                .merchantId(req.getMerchantId())
                .runnerId(req.getRunnerId())
                .grossCents(gross)
                .merchantCents(merchantCents)
                .runnerCents(runnerCents)
                .status(OrderSettlementStatus.SETTLED)
                .idempotencyKey(idem)
                .settledAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
        orderPaymentMapper.updateStatus(paid.getId(), OrderPaymentStatus.SETTLED);
        paid.setStatus(OrderPaymentStatus.SETTLED);
        return toPaymentDTO(paid, merchantCents, runnerCents);
    }

    private void validatePayRequest(WalletPayOrderRequest req) {
        if (req == null || req.getOrderId() == null || req.getUserId() == null || req.getMerchantId() == null || req.getAmountCents() == null || req.getAmountCents() <= 0) {
            throw new IllegalArgumentException("INVALID_PAY_ORDER_REQUEST");
        }
    }

    private WalletAccount ensureAccountForUpdate(WalletOwnerType ownerType, Long ownerId) {
        WalletAccount account = walletAccountMapper.findByOwnerForUpdate(ownerType, ownerId);
        if (account != null) {
            return account;
        }
        try {
            walletAccountMapper.insert(WalletAccount.builder()
                    .ownerType(ownerType)
                    .ownerId(ownerId)
                    .balanceCents(0L)
                    .frozenCents(0L)
                    .status("ACTIVE")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build());
        } catch (Exception ignore) {
            // another transaction may create it concurrently
        }
        account = walletAccountMapper.findByOwnerForUpdate(ownerType, ownerId);
        if (account == null) {
            throw new IllegalStateException("WALLET_ACCOUNT_CREATE_FAILED");
        }
        return account;
    }

    private WalletAccountDTO toAccountDTO(WalletAccount account) {
        if (account == null) {
            return null;
        }
        return WalletAccountDTO.builder()
                .ownerType(account.getOwnerType())
                .ownerId(account.getOwnerId())
                .balanceCents(account.getBalanceCents())
                .frozenCents(account.getFrozenCents())
                .build();
    }

    private WalletOrderPaymentDTO toPaymentDTO(OrderPayment payment, Long merchantCents, Long runnerCents) {
        if (payment == null) {
            return null;
        }
        return WalletOrderPaymentDTO.builder()
                .orderId(payment.getOrderId())
                .amountCents(payment.getAmountCents())
                .status(payment.getStatus())
                .merchantCents(merchantCents)
                .runnerCents(runnerCents)
                .build();
    }

    private long safeAdd(Long a, Long b) {
        long x = Objects.requireNonNullElse(a, 0L);
        long y = Objects.requireNonNullElse(b, 0L);
        return Math.addExact(x, y);
    }
}
