package com.foundly.app2.service;

import com.foundly.app2.dto.FoundItemReportRequest;
import com.foundly.app2.dto.LostItemReportRequest;
import com.foundly.app2.dto.LostItemPreviewDTO;
import com.foundly.app2.dto.ItemReportResponse;
import com.foundly.app2.entity.Category;
import com.foundly.app2.entity.FoundItemDetails;
import com.foundly.app2.entity.ItemReports;
import com.foundly.app2.entity.User;
import com.foundly.app2.repository.CategoryRepository;
import com.foundly.app2.repository.FoundItemDetailsRepository;
import com.foundly.app2.repository.ItemReportsRepository;
import com.foundly.app2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemReportsServiceTest {

    @Mock
    private ItemReportsRepository itemReportsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FoundItemDetailsRepository foundItemDetailsRepository;

    @Mock
    private TransactionsService transactionsService;

    @InjectMocks
    private ItemReportsService itemReportsService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private User user;
    private Category category;
    private ItemReports itemReport;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1);

        category = new Category();
        category.setCategoryId(1);

        itemReport = new ItemReports();
        itemReport.setItemId(1);
        itemReport.setUser(user);
        itemReport.setCategory(category);
        itemReport.setItemName("Test Item");
        itemReport.setType(ItemReports.Type.FOUND);
    }

    @Test
    public void testReportFoundItem_Success() {
        FoundItemReportRequest request = new FoundItemReportRequest();
        request.setItemName("Item1");
        request.setDescription("Description1");
        request.setLocation("Location1");
        request.setImageUrl("image.jpg");
        request.setDateLostOrFound("2025-04-15 16:21");
        request.setUserId(1);
        request.setCategoryId(1);
        request.setName("Reporter");
        request.setHandoverToSecurity(true);
        request.setSecurityId("100");
        request.setSecurityName("SecurityName");
        request.setPickupMessage("Pickup message");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(itemReportsRepository.save(any(ItemReports.class))).thenAnswer(i -> i.getArguments()[0]);
        when(foundItemDetailsRepository.save(any(FoundItemDetails.class))).thenReturn(null);

        ItemReports saved = itemReportsService.reportFoundItem(request);

        assertNotNull(saved);
        assertEquals("Item1", saved.getItemName());
        assertEquals(ItemReports.Type.FOUND, saved.getType());
    }

    @Test
    public void testReportLostItem_Success() {
        LostItemReportRequest request = new LostItemReportRequest();
        request.setItemName("LostItem1");
        request.setDescription("Lost Description");
        request.setLocation("Lost Location");
        request.setImageUrl("lostimage.jpg");
        request.setDateLostOrFound("2025-04-15 16:21");
        request.setUserId(1);
        request.setCategoryId(1);
        request.setName("Lost Reporter");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(itemReportsRepository.save(any(ItemReports.class))).thenAnswer(i -> i.getArguments()[0]);

        ItemReports lostItem = itemReportsService.reportLostItem(request);

        assertNotNull(lostItem);
        assertEquals("LostItem1", lostItem.getItemName());
        assertEquals(ItemReports.Type.LOST, lostItem.getType());
    }

    @Test
    public void testGetAllItemReports() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        when(itemReportsRepository.findAll()).thenReturn(reports);

        List<ItemReports> result = itemReportsService.getAllItemReports();

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
    }

    @Test
    public void testGetItemReportById_Found() {
        when(itemReportsRepository.findById(1)).thenReturn(Optional.of(itemReport));

        Optional<ItemReports> result = itemReportsService.getItemReportById(1);

        assertTrue(result.isPresent());
        assertEquals("Test Item", result.get().getItemName());
    }

    @Test
    public void testGetItemReportById_NotFound() {
        when(itemReportsRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ItemReports> result = itemReportsService.getItemReportById(1);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGetItemsByType() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        when(itemReportsRepository.findByType(ItemReports.Type.FOUND)).thenReturn(reports);

        List<ItemReports> result = itemReportsService.getItemsByType(ItemReports.Type.FOUND);

        assertEquals(1, result.size());
        assertEquals(ItemReports.Type.FOUND, result.get(0).getType());
    }

    @Test
    public void testGetRecentLostItemsPreview() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        Pageable pageable = PageRequest.of(0, 5);
        when(itemReportsRepository.findByTypeOrderByDateReportedDesc(ItemReports.Type.LOST, pageable)).thenReturn(reports);

        List<LostItemPreviewDTO> result = itemReportsService.getRecentLostItemsPreview(5);

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
    }

    @Test
    public void testGetLostReportsByUserId() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        when(itemReportsRepository.findByUser_UserIdAndType(1L, ItemReports.Type.LOST)).thenReturn(reports);

        List<ItemReportResponse> result = itemReportsService.getLostReportsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
    }

    @Test
    public void testGetFoundReportsByUserId() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        when(itemReportsRepository.findByUser_UserIdAndType(1L, ItemReports.Type.FOUND)).thenReturn(reports);

        List<ItemReportResponse> result = itemReportsService.getFoundReportsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getItemName());
    }

    @Test
    public void testDeleteItemReportsByUserId_WithReports() {
        List<ItemReports> reports = new ArrayList<>();
        reports.add(itemReport);
        when(itemReportsRepository.findByUserId(1L)).thenReturn(reports);
        doNothing().when(transactionsService).deleteTransactionsByItemIds(anyList());
        doNothing().when(itemReportsRepository).deleteAll(reports);

        itemReportsService.deleteItemReportsByUserId(1);

        verify(transactionsService).deleteTransactionsByItemIds(anyList());
        verify(itemReportsRepository).deleteAll(reports);
    }

    @Test
    public void testDeleteItemReportsByUserId_NoReports() {
        when(itemReportsRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        itemReportsService.deleteItemReportsByUserId(1);

        verify(transactionsService, never()).deleteTransactionsByItemIds(anyList());
        verify(itemReportsRepository, never()).deleteAll(anyList());
    }
}
