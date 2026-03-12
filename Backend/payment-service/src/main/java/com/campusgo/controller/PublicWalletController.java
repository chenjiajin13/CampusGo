package com.campusgo.controller;

import com.campusgo.dto.WalletAccountDTO;
import com.campusgo.dto.WalletTopupRequest;
import com.campusgo.dto.WalletTransactionDTO;
import com.campusgo.enums.WalletOwnerType;
import com.campusgo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments/wallet")
@RequiredArgsConstructor
public class PublicWalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public ResponseEntity<WalletAccountDTO> me(@RequestHeader("X-User-Id") Long userId,
                                               @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        WalletOwnerType ownerType = ownerTypeFromPrincipal(pt);
        if (ownerType == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(walletService.getWallet(ownerType, userId));
    }

    @GetMapping("/me/transactions")
    public ResponseEntity<List<WalletTransactionDTO>> meTransactions(@RequestHeader("X-User-Id") Long userId,
                                                                     @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                                     @RequestParam(value = "limit", defaultValue = "50") Integer limit) {
        WalletOwnerType ownerType = ownerTypeFromPrincipal(pt);
        if (ownerType == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(walletService.listTransactions(ownerType, userId, limit));
    }

    @PostMapping("/me/topup")
    public ResponseEntity<WalletAccountDTO> meTopup(@RequestHeader("X-User-Id") Long userId,
                                                    @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                    @RequestBody WalletTopupRequest req) {
        WalletOwnerType ownerType = ownerTypeFromPrincipal(pt);
        if (ownerType == null) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(walletService.topup(ownerType, userId, req.getAmountCents(), req.getIdempotencyKey(), req.getRemark()));
    }

    private WalletOwnerType ownerTypeFromPrincipal(String pt) {
        if (pt == null) return null;
        if ("USER".equalsIgnoreCase(pt)) return WalletOwnerType.USER;
        if ("MERCHANT".equalsIgnoreCase(pt)) return WalletOwnerType.MERCHANT;
        if ("RUNNER".equalsIgnoreCase(pt)) return WalletOwnerType.RUNNER;
        return null;
    }
}
