package eu.dev089;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonTemplates {

    public static JsonObject getValidProductAsJsonObject() {
        return JsonParser
                .parseString(
                        String.format(JSON, DEFAULT_SKU, DEFAULT_QUANTITY, DEFAULT_AMAZON_QUANTITY, DEFAULT_DELIVERY,
                                DEFAULT_LIEFERZEIT, DEFAULT_DELIVERY_WHEN_NA))
                .getAsJsonObject();
    }

    private static final String JSON = "{\n"
            + "  \"sku\": \"%s\",\n"
            + "  \"name\": \"Testprodukt\",\n"
            + "  \"extension_attributes\": {\n"
            + "    \"stock_item\": {\n"
            + "      \"qty\": %s\n"
            + "    }\n"
            + "  },\n"
            + "  \"custom_attributes\": [\n"
            + "    {\n"
            + "      \"attribute_code\": \"amazon_qty\",\n"
            + "      \"value\": \"%s\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"attribute_code\": \"delivery\",\n"
            + "      \"value\": \"%s\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"attribute_code\": \"lieferzeit\",\n"
            + "      \"value\": \"%s\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"attribute_code\": \"delivery_wenn_na\",\n"
            + "      \"value\": \"%s\"\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    public static final String DEFAULT_SKU = "007-James_Bond";
    public static final String DEFAULT_QUANTITY = "11";
    public static final String DEFAULT_AMAZON_QUANTITY = "12";
    public static final String DEFAULT_DELIVERY = "0";
    public static final String DEFAULT_LIEFERZEIT = "10";
    public static final String DEFAULT_DELIVERY_WHEN_NA = "0";

    public static JsonObject getWithQuantity(String quantity) {
        return JsonParser
                .parseString(
                        String.format(JSON, DEFAULT_SKU, quantity, DEFAULT_AMAZON_QUANTITY, DEFAULT_DELIVERY,
                                DEFAULT_LIEFERZEIT, DEFAULT_DELIVERY_WHEN_NA))
                .getAsJsonObject();
    }

    public static JsonObject getWithAmazonQuantity(String amazonQuantity) {
        return JsonParser
                .parseString(
                        String.format(JSON, DEFAULT_SKU, DEFAULT_QUANTITY, amazonQuantity, DEFAULT_DELIVERY,
                                DEFAULT_LIEFERZEIT, DEFAULT_DELIVERY_WHEN_NA))
                .getAsJsonObject();
    }

    public static JsonObject getWithDelivery(String delivery) {
        return JsonParser
                .parseString(
                        String.format(JSON, DEFAULT_SKU, DEFAULT_QUANTITY, DEFAULT_AMAZON_QUANTITY, delivery,
                                DEFAULT_LIEFERZEIT, DEFAULT_DELIVERY_WHEN_NA))
                .getAsJsonObject();
    }

    public static JsonObject getWithDeliveryWhenNa(String deliveryWhenNa) {
        return JsonParser
                .parseString(
                        String.format(JSON, DEFAULT_SKU, DEFAULT_QUANTITY, DEFAULT_AMAZON_QUANTITY, DEFAULT_DELIVERY,
                                DEFAULT_LIEFERZEIT, deliveryWhenNa))
                .getAsJsonObject();
    }
}
