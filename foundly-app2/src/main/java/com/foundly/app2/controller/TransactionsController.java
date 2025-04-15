package com.foundly.app2.controller;

import com.foundly.app2.entity.Transactions;
import com.foundly.app2.service.TransactionsService;
import com.foundly.app2.dto.ClaimRequest;
import com.foundly.app2.dto.HandoverRequest;
import com.foundly.app2.dto.NotificationDTO;
import com.foundly.app2.dto.TransactionResponse;
import com.foundly.app2.dto.TransactionResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/claims/{userId}")
    public ResponseEntity<List<TransactionResponse>> getClaimsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionsService.getClaimsByUserId(userId));
    }

    @GetMapping("/handovers/{userId}")
    public ResponseEntity<List<TransactionResponse>> getHandoversByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionsService.getHandoversByUserId(userId));
    }
    @PutMapping("/reporter-completed/{transactionId}")
    public ResponseEntity<Transactions> reporterCompleted(@PathVariable Integer transactionId) {
        return ResponseEntity.ok(transactionsService.updateReporterCompletion(transactionId));
    }
    

    @PutMapping("/requester-completed/{transactionId}")
    public ResponseEntity<Transactions> requesterCompleted(@PathVariable Integer transactionId) {
        return ResponseEntity.ok(transactionsService.updateRequesterCompletion(transactionId));
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Integer userId) {
        List<NotificationDTO> notifications = transactionsService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }



    // Additional methods for handling transaction status updates can be added here
}