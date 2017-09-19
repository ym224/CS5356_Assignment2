package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.tables.records.ReceiptsRecord;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.math.BigDecimal;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class ReceiptResponseTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private static final String jsonString =
            "{\"id\": 5, \"merchant\": \"Gerry's Ice Cream\", \"amount\": 22.50}";
    //@Test
    public void serializesToJSON() throws Exception {
        ReceiptsRecord receiptsRecord = new ReceiptsRecord();
        receiptsRecord.setId(1);
        receiptsRecord.setAmount(BigDecimal.ONE);
        receiptsRecord.setMerchant("merchant");
        ReceiptResponse receiptResponse = new ReceiptResponse(receiptsRecord);

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/person.json"), ReceiptResponse.class));

        assertThat(MAPPER.writeValueAsString(receiptResponse)).isEqualTo(expected);
    }

    //@Test
    public void deserializesFromJSON() throws Exception {
        ReceiptsRecord receiptsRecord = new ReceiptsRecord();
        receiptsRecord.setId(1);
        receiptsRecord.setAmount(BigDecimal.ONE);
        receiptsRecord.setMerchant("merchant");
        ReceiptResponse receiptResponse = new ReceiptResponse(receiptsRecord);
        assertThat(MAPPER.readValue(jsonString, ReceiptResponse.class))
                .isEqualTo(receiptResponse);
    }

}
