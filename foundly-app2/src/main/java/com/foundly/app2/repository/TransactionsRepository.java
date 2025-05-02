package com.foundly.app2.repository;

import com.foundly.app2.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
	// Simple count by transaction type
	long countByTransactionType(Transactions.TransactionType transactionType);

	// Group count by transaction status (JPQL)
	@Query("SELECT t.transactionStatus, COUNT(t) FROM Transactions t GROUP BY t.transactionStatus")
	List<Object[]> countGroupedByTransactionStatus();


    // Custom query method to find Transactions by TransactionStatus
    List<Transactions> findByTransactionStatus(Transactions.TransactionStatus transactionStatus);
    
    List<Transactions> findByRequesterUserId(Long userId);

    List<Transactions> findByRequesterUserIdAndTransactionType(Long userId, Transactions.TransactionType type);
    
    List<Transactions> findByReporterUserId(Integer userId);

    List<Transactions> findByItem_ItemIdIn(List<Integer> itemIds);

    // Custom query method to update the transaction status
    @Query("UPDATE Transactions t SET t.transactionStatus = :transactionStatus WHERE t.transactionId = :transactionId")
    @Modifying
    void updateTransactionStatus(@Param("transactionId") Integer transactionId, @Param("transactionStatus") Transactions.TransactionStatus transactionStatus);
}
