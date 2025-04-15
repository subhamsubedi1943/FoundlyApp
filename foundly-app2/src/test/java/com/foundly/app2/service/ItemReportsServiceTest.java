package com.foundly.app2.service;

import com.foundly.app2.dto.FoundItemReportRequest;
import com.foundly.app2.dto.LostItemReportRequest;
import com.foundly.app2.entity.Category;
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

import java.time.format.DateTimeFormatter;
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

    @InjectMocks
    private ItemReportsService itemReportsService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        User user = new User();
        user.setUserId(1);

        Category category = new Category();
        category.setCategoryId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(itemReportsRepository.save(any(ItemReports.class))).thenAnswer(i -> i.getArguments()[0]);
        when(foundItemDetailsRepository.save(any())).thenReturn(null);

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

        User user = new User();
        user.setUserId(1);

        Category category = new Category();
        category.setCategoryId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(itemReportsRepository.save(any(ItemReports.class))).thenAnswer(i -> i.getArguments()[0]);

        ItemReports lostItem = itemReportsService.reportLostItem(request);

        assertNotNull(lostItem);
        assertEquals("LostItem1", lostItem.getItemName());
        assertEquals(ItemReports.Type.LOST, lostItem.getType());
    }
}
