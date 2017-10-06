package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

            String merchantName = null;
            BigDecimal amount = null;
            boolean isMerchantNameSet = false;

            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
            List<String> receiptTexts = new ArrayList<>();
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                //out.printf("Position : %s\n", annotation.getBoundingPoly());
                //out.printf("Text: %s\n", annotation.getDescription());
                //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
                //annotation.getAllFields().forEach((k, v) -> out.printf("%s : %s\n", k, v.toString()));
                String text = annotation.getDescription();
                if (!isMerchantNameSet && text.length() < 20 && !isDecimalText(text)) {
                    merchantName = text;
                    isMerchantNameSet = true;
                }
                if (isDecimalText(text)) {
                    try {
                        amount = new BigDecimal(text);
                    } catch (Exception NumberFormatException) {
                        System.err.println(text + " is not a currency.");
                    }
                }
            }
            System.out.println("merchant " + merchantName);
            System.out.println("amt " + amount);

            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }

    boolean isDecimalText(String text){
        String[] splitStrings = text.split("\\.");
        return (splitStrings.length == 2 && splitStrings[1].length() == 2);
    }
}