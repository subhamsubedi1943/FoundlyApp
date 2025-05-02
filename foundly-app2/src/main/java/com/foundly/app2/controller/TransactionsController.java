package com.foundly.app2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foundly.app2.dto.ClaimRequest;
import com.foundly.app2.dto.HandoverRequest;
import com.foundly.app2.dto.NotificationDTO;
import com.foundly.app2.dto.TransactionResponse;
import com.foundly.app2.dto.TransactionResponseDTO;
import com.foundly.app2.entity.Transactions;
import com.foundly.app2.service.TransactionsService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/transactions")
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    // Get all transactions
    @GetMapping
    public ResponseEntity<List<Transactions>> getAllTransactions() {
        List<Transactions> transactions = transactionsService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Get a transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transactions> getTransactionById(@PathVariable Integer id) {
        return transactionsService.getTransactionById(id)
                .map(transaction -> new ResponseEntity<>(transaction, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Claim an item
    @PostMapping("/claim")
    public ResponseEntity<Transactions> claimItem(@RequestBody ClaimRequest request) {
        Transactions createdTransaction = transactionsService.claimItem(request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    // Handover an item
    @PostMapping("/handover")
    public ResponseEntity<TransactionResponseDTO> handoverItem(@RequestBody HandoverRequest request) {
        Transactions createdTransaction = transactionsService.handoverItem(request);

        TransactionResponseDTO dto = new TransactionResponseDTO(
            createdTransaction.getTransactionId(),
            createdTransaction.getItem().getItemName(),
            createdTransaction.getRequesterName(),
            createdTransaction.getDescription(),
            createdTransaction.getTransactionStatus().name(),
            createdTransaction.getTransactionType().name(),
            createdTransaction.getDateUpdated()
        );

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // Get claims by user ID
    @GetMapping("/claims/{userId}")
    public ResponseEntity<List<TransactionResponse>> getClaimsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionsService.getClaimsByUserId(userId));
    }

    // Get handovers by user ID
    @GetMapping("/handovers/{userId}")
    public ResponseEntity<List<TransactionResponse>> getHandoversByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionsService.getHandoversByUserId(userId));
    }

    // Mark the reporter as completed for a transaction
    @PutMapping("/reporter-completed/{transactionId}")
    public ResponseEntity<Transactions> reporterCompleted(@PathVariable Integer transactionId) {
        Transactions updatedTransaction = transactionsService.updateReporterCompletion(transactionId);
        return ResponseEntity.ok(updatedTransaction);
    }

    // Mark the requester as completed for a transaction
    @PutMapping("/requester-completed/{transactionId}")
    public ResponseEntity<Transactions> requesterCompleted(@PathVariable Integer transactionId) {
        Transactions updatedTransaction = transactionsService.updateRequesterCompletion(transactionId);
        return ResponseEntity.ok(updatedTransaction);
    }

    // Get notifications for a user based on their ID
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Integer userId) {
        List<NotificationDTO> notifications = transactionsService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/count/claims")
    public ResponseEntity<Long> getTotalClaimsCount() {
        long count = transactionsService.countByTransactionType(Transactions.TransactionType.CLAIM);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/count/handovers")
    public ResponseEntity<Long> getTotalHandoversCount() {
        long count = transactionsService.countByTransactionType(Transactions.TransactionType.HANDOVER);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/count/status")
    public ResponseEntity<Map<String, Long>> getTransactionCountByStatus() {
        Map<String, Long> statusCounts = transactionsService.countTransactionsGroupedByStatus();
        return ResponseEntity.ok(statusCounts);
    }


}
