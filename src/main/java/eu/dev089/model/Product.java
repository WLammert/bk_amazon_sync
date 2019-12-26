package eu.dev089.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Product {

    public static final Integer STANDARD_DELIVERY_WHENN_NA = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);
    public static final int STANDARD_AMAZON_QTY = 33;
    private final int magentoQuantity;
    private final int deliveryStandard;
    private final String sku;
    private int amazonQuantity;
    private int amazonDelivery;

    public boolean isUpdatable() {
        if (magentoQuantity <= 0) {
            if (amazonDelivery != deliveryStandard || amazonQuantity != STANDARD_AMAZON_QTY) {
                amazonQuantity = STANDARD_AMAZON_QTY;
                amazonDelivery = deliveryStandard;
                return true;
            }
        } else {
            if (amazonDelivery != 0 || amazonQuantity != magentoQuantity) {
                amazonQuantity = magentoQuantity;
                amazonDelivery = 0;
                return true;
            }
            return false;
        }
        return false;
    }

    private Product(ProductBuilder builder) {
        this.magentoQuantity = builder.magentoQuantity;
        this.amazonDelivery = builder.amazonDelivery;
        this.amazonQuantity = builder.amazonQuantity;
        this.deliveryStandard = builder.deliveryStandard;
        this.sku = builder.sku;
    }

    public String getSku() {
        return sku;
    }

    public int getMagentoQuantity() {
        return magentoQuantity;
    }

    public int getDeliveryStandard() {
        return deliveryStandard;
    }

    public int getAmazonQuantity() {
        return amazonQuantity;
    }

    public int getAmazonDelivery() {
        return amazonDelivery;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    public static final class ProductBuilder {

        private Integer magentoQuantity;
        private Integer amazonQuantity;
        private Integer amazonDelivery;
        private Integer deliveryStandard;
        private String sku;

        ProductBuilder() {
        }

        public ProductBuilder parseJson(JsonObject json) {
            this.magentoQuantity = parseQuantity(json);

            for (JsonElement element : json.get("custom_attributes").getAsJsonArray()) {
                switch (element.getAsJsonObject().get("attribute_code").getAsString()) {
                    case "amazon_qty":
                        this.amazonQuantity = getValueAsInteger(element, "amazon_qty");
                        break;
                    case "delivery":
                        this.amazonDelivery = getValueAsInteger(element, "delivery");
                        break;
                    case "delivery_wenn_na":
                        this.deliveryStandard = getValueAsInteger(element, "delivery_wenn_na");
                        break;
                }
            }
            return this;
        }

        int parseQuantity(JsonObject json) {
            var quantity = 0;
            try {
                quantity = json.get("extension_attributes")
                        .getAsJsonObject().get("stock_item")
                        .getAsJsonObject().get("qty").getAsInt();
            } catch (Exception e) {
                LOGGER.warn("SKU {} had no valid quantity, set to 0, please check!", sku);
            }
            return quantity;
        }

        Integer getValueAsInteger(JsonElement element, String fieldName) {
            var intValue = 0;
            try {
                intValue = element.getAsJsonObject().get("value").getAsInt();
            } catch (NullPointerException | NumberFormatException e) {
                LOGGER.warn("SKU {}: Nichtnumerischer Wert im Feld {}, bitte korrigieren!", this.sku, fieldName);
                if (fieldName.equals("delivery_wenn_na")) {
                    intValue = STANDARD_DELIVERY_WHENN_NA;
                }
            }
            return intValue;
        }

        public ProductBuilder setMagentoQuantity(int qty) {
            this.magentoQuantity = qty;
            return this;
        }

        public ProductBuilder setAmazonQuantity(int qty) {
            this.amazonQuantity = qty;
            return this;
        }

        public ProductBuilder setAmazonDelivery(int days) {
            this.amazonDelivery = days;
            return this;
        }

        public ProductBuilder setDeliveryStandard(int days) {
            this.deliveryStandard = days;
            return this;
        }

        public ProductBuilder setSku(String sku) {
            this.sku = sku;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
