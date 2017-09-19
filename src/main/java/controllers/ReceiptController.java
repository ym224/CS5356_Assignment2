package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptController {
    final ReceiptDao receipts;
    final TagDao tags;

    public ReceiptController(ReceiptDao receipts, TagDao tags) {
        this.tags = tags;
        this.receipts = receipts;
    }

    @POST
    @Path("/receipts")
    public int createReceipt(@Valid @NotNull CreateReceiptRequest receipt) {
        return receipts.insert(receipt.merchant, receipt.amount);
    }

    @GET
    @Path("/receipts")
    public List<ReceiptResponse> getReceipts() {
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();
        List<ReceiptResponse> receiptResponses = receiptRecords.stream().map(ReceiptResponse::new).collect(toList());
        receiptResponses.forEach(receiptResponse ->
                receiptResponse.getTags().addAll(
                        receipts.getTagNamesForReceiptId(receiptResponse.getId())));
        return receiptResponses;
    }

    @PUT
    @Path("/tags/{tag}")
    public Response toggleTag(@PathParam("tag") String tagName, Integer receiptId) {
        // return 404 if no receipt id doesn't exist
        if (!receipts.idExists(receiptId)) {
            throw new WebApplicationException("receipt id does not exist", Response.Status.NOT_FOUND);
        }
        // find or create tag by name
        Integer tagId = tags.getTagIdFromName(tagName);
        if (tagId == null) {
            tagId = tags.insert(tagName);
        }
        receipts.toggleTagReceipt(receiptId, tagId);
        return Response.ok().build();
    }

    @GET
    @Path("/tags/{tag}")
    public List<ReceiptResponse> getReceipts(@PathParam("tag") String tagName) {
        Integer tagId = tags.getTagIdFromName(tagName);
        List<ReceiptsRecord> receiptsRecords = receipts.getReceiptsForTag(tagId);
        return receiptsRecords.stream().map(ReceiptResponse::new).collect(toList());
    }

    @DELETE
    @Path("/tags/{tag}")
    public Response getReceipts(@PathParam("tag") String tagName, Integer receiptId) {
        // return 404 if no receipt id doesn't exist
        if (!receipts.idExists(receiptId)) {
            throw new WebApplicationException("receipt id does not exist", Response.Status.NOT_FOUND);
        }
        Integer tagId = tags.getTagIdFromName(tagName);
        if (tagId == null ) {
            throw new WebApplicationException("receipt id does not exist", Response.Status.NOT_FOUND);
        }
        receipts.deleteTagReceipt(receiptId, tagId);
        return Response.ok().build();
    }

    @GET
    @Path("/netid")
    public String getNetId() {
        return "ym224";
    }

}
