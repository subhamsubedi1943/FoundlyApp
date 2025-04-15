package com.foundly.app2.service;

import com.foundly.app2.dto.ClaimRequest;
import com.foundly.app2.dto.HandoverRequest;
import com.foundly.app2.dto.TransactionResponse;
import com.foundly.app2.entity.ItemReports;
import com.foundly.app2.entity.Transactions;
import com.foundly.app2.entity.User;
import com.foundly.app2.repository.ItemReportsRepository;
import com.foundly.app2.repository.TransactionsRepository;
import com.foundly.app2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionsServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private ItemReportsRepository itemReportsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionsService transactionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTransactions() {
        Transactions t1 = new Transactions();
        t1.setTransactionId(1);
        Transactions t2 = new Transactions();
        t2.setTransactionId(2);

        when(transactionsRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Transactions> transactions = transactionsService.getAllTransactions();

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        verify(transactionsRepository, times(1)).findAll();
    }

    @Test
    public void testGetTransactionById_Found() {
        Transactions t = new Transactions();
        t.setTransactionId(1);

        when(transactionsRepository.findById(1)).thenReturn(Optional.of(t));

        Optional<Transactions> result = transactionsService.getTransactionById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getTransactionId());
        verify(transactionsRepository, times(1)).findById(1);
    }

    @Test
    public void testGetTransactionById_NotFound() {
        when(transactionsRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Transactions> result = transactionsService.getTransactionById(1);

        assertFalse(result.isPresent());
        verify(transactionsRepository, times(1)).findById(1);
    }

    @Test
    public void testClaimItem() {
        ClaimRequest request = new ClaimRequest();
        request.setItemId(1);
        request.setRequesterId(1);
        request.setEmployeeId("E123");
        request.setName("Requester");
        request.setPhoto("photo.jpg");
        request.setDescription("desc");

        ItemReports item = new ItemReports();
        item.setItemId(1);
        item.setItemStatus(ItemReports.ItemStatus.WITH_SECURITY);
        User user = new User();
        item.setUser(user);

        User requester = new User();
        requester.setUserId(1);

        when(itemReportsRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(1)).thenReturn(Optional.of(requester));
        when(transactionsRepository.save(any(Transactions.class))).thenAnswer(i -> i.getArguments()[0]);

        Transactions transaction = transactionsService.claimItem(request);

        assertNotNull(transaction);
        assertEquals(Transactions.TransactionStatus.PENDING_COMPLETION, transaction.getTransactionStatus());
        verify(transactionsRepository, times(1)).save(any(Transactions.class));
    }

    @Test
    public void testHandoverItem() {
        HandoverRequest request = new HandoverRequest();
        request.setItemId(1);
        request.setRequesterId(1);
        request.setPhoto("photo.jpg");
        request.setDescription("desc");
        request.setHandoverToSecurity(true);
        request.setSecurityId("100");
        request.setSecurityName("SecurityName");

        ItemReports item = new ItemReports();
        item.setItemId(1);
        User user = new User();
        item.setUser(user);

        User requester = new User();
        requester.setUserId(1);
        requester.setEmployeeId("E123");
        requester.setName("Requester");

        when(itemReportsRepository.findById(1)).thenReturn(Optional.of(item));
        when(userRepository.findById(1)).thenReturn(Optional.of(requester));
        when(itemReportsRepository.save(any(ItemReports.class))).thenReturn(item);
        when(transactionsRepository.save(any(Transactions.class))).thenAnswer(i -> i.getArguments()[0]);

        Transactions transaction = transactionsService.handoverItem(request);

        assertNotNull(transaction);
        assertEquals(Transactions.TransactionType.HANDOVER, transaction.getTransactionType());
        assertTrue(transaction.isHandedOverToSecurity());
        verify(transactionsRepository, times(1)).save(any(Transactions.class));
    }

    @Test
    public void testGetClaimsByUserId() {
        Transactions transaction = new Transactions();
        ItemReports item = new ItemReports();
        item.setItemId(1);
        item.setType(ItemReports.Type.LOST);
        // Provide a non-null Category object to avoid NullPointerException
        com.foundly.app2.entity.Category category = new com.foundly.app2.entity.Category();
        category.setCategoryName("Lost Category");
        item.setCategory(category);
        item.setLocation("Location");
        transaction.setItem(item);
        transaction.setTransactionId(1);
        transaction.setTransactionType(Transactions.TransactionType.CLAIM);
        transaction.setTransactionStatus(Transactions.TransactionStatus.REQUESTED);
        transaction.setDescription("desc");
        transaction.setPhoto("photo.jpg");
        transaction.setHandedOverToSecurity(false);
        transaction.setPickupMessage(null);
        transaction.setSecurityId(null);
        transaction.setSecurityName(null);

        when(transactionsRepository.findByRequesterUserIdAndTransactionType(1L, Transactions.TransactionType.CLAIM))
                .thenReturn(Arrays.asList(transaction));

        List<TransactionResponse> responses = transactionsService.getClaimsByUserId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(transactionsRepository, times(1)).findByRequesterUserIdAndTransactionType(1L, Transactions.TransactionType.CLAIM);
    }

    @Test
    public void testGetHandoversByUserId() {
        Transactions transaction = new Transactions();
        ItemReports item = new ItemReports();
        item.setItemId(1);
        item.setType(ItemReports.Type.FOUND);
        // Provide a non-null Category object to avoid NullPointerException
        com.foundly.app2.entity.Category category = new com.foundly.app2.entity.Category();
        category.setCategoryName("Found Category");
        item.setCategory(category);
        item.setLocation("Location");
        transaction.setItem(item);
        transaction.setTransactionId(1);
        transaction.setTransactionType(Transactions.TransactionType.HANDOVER);
        transaction.setTransactionStatus(Transactions.TransactionStatus.REQUESTED);
        transaction.setDescription("desc");
        transaction.setPhoto("photo.jpg");
        transaction.setHandedOverToSecurity(true);
        transaction.setPickupMessage("Pickup");
        transaction.setSecurityId("100");
        transaction.setSecurityName("SecurityName");

        when(transactionsRepository.findByRequesterUserIdAndTransactionType(1L, Transactions.TransactionType.HANDOVER))
                .thenReturn(Arrays.asList(transaction));

        List<TransactionResponse> responses = transactionsService.getHandoversByUserId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(transactionsRepository, times(1)).findByRequesterUserIdAndTransactionType(1L, Transactions.TransactionType.HANDOVER);
    }
}
