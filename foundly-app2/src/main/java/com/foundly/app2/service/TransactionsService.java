package com.foundly.app2.service;

import com.foundly.app2.entity.Transactions;
import com.foundly.app2.entity.ItemReports;
import com.foundly.app2.entity.User;
import com.foundly.app2.repository.TransactionsRepository;
import com.foundly.app2.repository.ItemReportsRepository;
import com.foundly.app2.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.foundly.app2.dto.ClaimRequest;
import com.foundly.app2.dto.HandoverRequest;
import com.foundly.app2.dto.NotificationDTO;
import com.foundly.app2.dto.TransactionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//import java.time.LocalDateTime;
import java.time.Duration;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private ItemReportsRepository itemReportsRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all transactions
    public List<Transactions> getAllTransactions() {
        return transactionsRepository.findAll();
    }

    // Get a transaction by ID
    public Optional<Transactions> getTransactionById(Integer transactionId) {
        return transactionsRepository.findById(transactionId);
    }

 // Claim an item
 // Claim an item
    @Transactional
    public Transactions claimItem(ClaimRequest request) {
        // Fetch item
        ItemReports item = itemReportsRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Fetch user
        User requester = userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        // Determine transaction status based on itemStatus
        Transactions.TransactionStatus status;
        if (item.getItemStatus() == ItemReports.ItemStatus.WITH_SECURITY) {
            status = Transactions.TransactionStatus.PENDING_COMPLETION;
        } else if (item.getItemStatus() == ItemReports.ItemStatus.WITH_FINDER) {
            status = Transactions.TransactionStatus.REQUESTED;
        } else {
            throw new IllegalStateException("Item is not available for claiming");
        }

        // Build transaction
        Transactions transaction = new Transactions();
        transaction.setItem(item);
        transaction.setRequester(requester);
        transaction.setReporter(item.getUser());
        transaction.setEmployeeId(request.getEmployeeId());
        transaction.setRequesterName(request.getName());
        transaction.setPhoto(request.getPhoto());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(Transactions.TransactionType.CLAIM);
        transaction.setTransactionStatus(status);
        transaction.setDateUpdated(LocalDateTime.now());

        return transactionsRepository.save(transaction);
    }

    // Handover an item
    @Transactional
    public Transactions handoverItem(HandoverRequest request) {
        ItemReports item = itemReportsRepository.findById(request.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));

        User requester = userRepository.findById(request.getRequesterId())
            .orElseThrow(() -> new RuntimeException("Requester not found"));

        Transactions transaction = new Transactions();
        transaction.setItem(item);
        transaction.setRequester(requester);
        transaction.setReporter(item.getUser());
        transaction.setEmployeeId(requester.getEmployeeId());
        transaction.setRequesterName(requester.getName());
        transaction.setPhoto(request.getPhoto());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionType(Transactions.TransactionType.HANDOVER);
        transaction.setDateUpdated(LocalDateTime.now());

        if (request.isHandoverToSecurity()) {
            item.setItemStatus(ItemReports.ItemStatus.WITH_SECURITY);
            transaction.setTransactionStatus(Transactions.TransactionStatus.PENDING_COMPLETION);
            transaction.setHandedOverToSecurity(true);
            transaction.setSecurityId(request.getSecurityId());
            transaction.setSecurityName(request.getSecurityName());
        } else {
            item.setItemStatus(ItemReports.ItemStatus.WITH_FINDER);
            transaction.setTransactionStatus(Transactions.TransactionStatus.REQUESTED);
            transaction.setHandedOverToSecurity(false);
            transaction.setPickupMessage(request.getPickupMessage());
        }

        itemReportsRepository.save(item);
        return transactionsRepository.save(transaction);
    }
    @Transactional
    public Transactions updateReporterCompletion(Integer transactionId) {
        Transactions transaction = transactionsRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setReporterCompleted(true);
        transaction.setDateUpdated(LocalDateTime.now());

        checkAndMarkCompletion(transaction);

        return transactionsRepository.save(transaction);
    }
    @Transactional
    public Transactions updateRequesterCompletion(Integer transactionId) {
        Transactions transaction = transactionsRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setRequesterCompleted(true);
        transaction.setDateUpdated(LocalDateTime.now());

        checkAndMarkCompletion(transaction);

        return transactionsRepository.save(transaction);
    }
    private void checkAndMarkCompletion(Transactions transaction) {
        boolean toSecurity = transaction.isHandedOverToSecurity();

        if (toSecurity && transaction.isRequesterCompleted()) {
            transaction.setTransactionStatus(Transactions.TransactionStatus.COMPLETED);
            transaction.getItem().setItemStatus(ItemReports.ItemStatus.RECEIVED);
            itemReportsRepository.save(transaction.getItem());
        }

        if (!toSecurity && transaction.isRequesterCompleted() && transaction.isReporterCompleted()) {
            transaction.setTransactionStatus(Transactions.TransactionStatus.COMPLETED);
            transaction.getItem().setItemStatus(ItemReports.ItemStatus.RECEIVED);
            itemReportsRepository.save(transaction.getItem());
        }
    }



        

        public List<NotificationDTO> getNotificationsForUser(Integer userId) {
            List<Transactions> transactions = transactionsRepository.findByReporterUserId(userId);

            List<NotificationDTO> notifications = new ArrayList<>();

            for (Transactions tx : transactions) {
                NotificationDTO dto = new NotificationDTO();
                dto.setTransactionId(tx.getTransactionId());
                dto.setType(tx.getTransactionType().toString());
                dto.setTime(getTimeAgo(tx.getDateUpdated()));
                dto.setDescription(tx.getDescription());
                dto.setPhoto(tx.getPhoto());
                dto.setItemStatus(tx.getItem().getItemStatus().toString());

                if (tx.getTransactionType() == Transactions.TransactionType.CLAIM) {
                    dto.setTitle("Claim Request: " + tx.getItem().getItemName());
                    dto.setMessage("A user has claimed the item you reported. Review the claim and respond.");
                } else if (tx.getTransactionType() == Transactions.TransactionType.HANDOVER) {
                    dto.setTitle("Good news! " + tx.getItem().getItemName() + " found");
                    dto.setMessage("A user found your lost item and wants to hand it over.");
                    dto.setPickupMessage(tx.getPickupMessage());
                    dto.setSecurityId(tx.getSecurityId());
                    dto.setSecurityName(tx.getSecurityName());
                }

                notifications.add(dto);
            }

            return notifications;
        }

        private String getTimeAgo(LocalDateTime dateTime) {
            // Optional helper - returns string like "2h ago", or "1d ago"
            Duration duration = Duration.between(dateTime, LocalDateTime.now());
            long hours = duration.toHours();
            if (hours < 24) return hours + "h ago";
            return (hours / 24) + "d ago";
        }
    
    
    public List<TransactionResponse> getClaimsByUserId(Long userId) {
        List<Transactions> transactions = transactionsRepository.findByRequesterUserIdAndTransactionType(userId, Transactions.TransactionType.CLAIM);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getHandoversByUserId(Long userId) {
        List<Transactions> transactions = transactionsRepository.findByRequesterUserIdAndTransactionType(userId, Transactions.TransactionType.HANDOVER);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transactions transaction) {
        return new TransactionResponse(
            transaction.getTransactionId(),
            transaction.getItem().getItemId(),
            transaction.getItem().getItemName(),
            transaction.getItem().getType().toString(),
            transaction.getItem().getCategory().getCategoryName(),
            transaction.getItem().getLocation(),
            transaction.getTransactionType(),
            transaction.getTransactionStatus(),
            transaction.getDescription(),
            transaction.getPhoto(),
            transaction.isHandedOverToSecurity(),
            transaction.getPickupMessage(),
            transaction.getSecurityId(),
            transaction.getSecurityName()
        );
    }

}