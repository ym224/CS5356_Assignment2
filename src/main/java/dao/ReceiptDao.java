package dao;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.ReceiptsTagsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.Tables.RECEIPTS_TAGS;
import static generated.tables.Tags.TAGS;

public class ReceiptDao {
    DSLContext dsl;

    public ReceiptDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String merchantName, BigDecimal amount) {
        ReceiptsRecord receiptsRecord = dsl
                .insertInto(RECEIPTS, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT)
                .values(merchantName, amount)
                .returning(RECEIPTS.ID)
                .fetchOne();

        checkState(receiptsRecord != null && receiptsRecord.getId() != null, "Insert into Receipts failed");

        return receiptsRecord.getId();
    }

    public List<ReceiptsRecord> getAllReceipts() {
        return dsl.selectFrom(RECEIPTS).fetch();
    }

    public boolean idExists(Integer receiptId){
        return dsl.fetchExists(RECEIPTS, RECEIPTS.ID.eq(receiptId));
    }

    public void toggleTagReceipt(Integer receiptId, Integer tagId) {
        // find record in receipts_tags table
        List<ReceiptsTagsRecord> receiptsTagsRecords = dsl.selectFrom(RECEIPTS_TAGS)
                .where(RECEIPTS_TAGS.RECEIPT_ID.eq(receiptId).and(RECEIPTS_TAGS.TAG_ID.eq(tagId))).fetch();

        // delete if entry exists with receipt and tag
        if (receiptsTagsRecords.size() > 0) {
            deleteTagReceipt(receiptId, tagId);
        }
        // otherwise, create new entry with receipt and tag
        else {
            dsl.insertInto(RECEIPTS_TAGS, RECEIPTS_TAGS.RECEIPT_ID, RECEIPTS_TAGS.TAG_ID)
                    .values(receiptId, tagId).execute();
        }
    }

    public void deleteTagReceipt(Integer receiptId, Integer tagId){
        dsl.delete(RECEIPTS_TAGS)
                .where(RECEIPTS_TAGS.RECEIPT_ID.eq(receiptId))
                .and(RECEIPTS_TAGS.TAG_ID.eq(tagId))
                .execute();
    }

    public List<ReceiptsRecord> getReceiptsForTag(Integer tagId) {
        Result<Record3<Integer, String, BigDecimal>> result = dsl.select(RECEIPTS.ID, RECEIPTS.MERCHANT, RECEIPTS.AMOUNT).from(RECEIPTS)
                .innerJoin(RECEIPTS_TAGS).on(RECEIPTS.ID.eq(RECEIPTS_TAGS.RECEIPT_ID))
                .where(RECEIPTS_TAGS.TAG_ID.eq(tagId))
                .fetch();

        List<ReceiptsRecord> receiptsRecords = new ArrayList<>();

        for (Record3 r: result) {
            ReceiptsRecord receiptsRecord = new ReceiptsRecord();
            receiptsRecord.setId((Integer)r.getValue(0));
            receiptsRecord.setMerchant((String)r.getValue(1));
            receiptsRecord.setAmount((BigDecimal) r.getValue(2));
            receiptsRecords.add(receiptsRecord);
        }

        return receiptsRecords;
    }

    public List<String> getTagNamesForReceiptId(Integer receiptId) {
        Result<Record1<String>> result = dsl.select(TAGS.NAME).from(TAGS)
                .innerJoin(RECEIPTS_TAGS).on(TAGS.ID.eq(RECEIPTS_TAGS.TAG_ID))
                .where(RECEIPTS_TAGS.RECEIPT_ID.eq(receiptId))
                .fetch();

        List<String> tagNames = new ArrayList<>();

        for (Record1 r: result) {
            tagNames.add((String)r.getValue(0));
        }
        return tagNames;
    }
}
