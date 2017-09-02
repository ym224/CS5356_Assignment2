package dao;

import generated.tables.records.ReceiptsRecord;
import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.RECEIPTS;
import static generated.tables.Tags.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String tagName) {
        TagsRecord tagsRecord = dsl
                .insertInto(TAGS, TAGS.NAME)
                .values(tagName)
                .returning(TAGS.ID)
                .fetchOne();

        checkState(tagsRecord != null && tagsRecord.getId() != null, "Insert into Tags failed");

        System.out.println("tagId after insertion is " + tagsRecord.getId());
        return tagsRecord.getId();
    }

    public List<TagsRecord> getAllTags() {
        return dsl.selectFrom(TAGS).fetch();
    }

    public Integer getTagIdFromName(String tagName) {
        return dsl.selectFrom(TAGS)
                .where(TAGS.NAME.eq(tagName))
                .fetchOne(TAGS.ID);
    }
}
