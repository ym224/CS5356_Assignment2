package controllers;

import api.ReceiptResponse;
import com.google.common.collect.ImmutableList;
import controllers.ReceiptController;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GetReceiptRequestTest{

    private final ReceiptsRecord receiptsRecord1 = new ReceiptsRecord();
    private final ReceiptsRecord receiptsRecord2 = new ReceiptsRecord();
    private final List<ReceiptsRecord> receiptsRecords = ImmutableList.of(receiptsRecord1, receiptsRecord2);
    private final List<ReceiptResponse> receiptResponses = ImmutableList.of(new ReceiptResponse(receiptsRecord1), new ReceiptResponse(receiptsRecord2));

    private static final ReceiptDao receiptDao = mock(ReceiptDao.class);
    private static final TagDao tagDao = mock(TagDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ReceiptController(receiptDao, tagDao))
            .build();

    @Before
    public void setup() {
        when(receiptDao.getAllReceipts()).thenReturn(receiptsRecords);
    }

    @After
    public void tearDown(){
        reset(receiptDao, tagDao);
    }

    //@Test
    public void testGetReceipts() {
        Response response = resources.client().target("/receipts").request().get();
        System.out.println(response);
        assertThat(resources.client().target("/receipts").request().get(List.class))
                .isEqualTo(receiptResponses);
        verify(receiptDao).getAllReceipts();
    }

}
