package eu.dev089.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Product {

    private static final Logger LOGGER = LogManager.getLogger(Product.class);
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

        public ProductBuilder setJson(JsonObject json) {
            if (jsonSuccessfullyParsed(json)) {
                return this;
            } else {
                LOGGER.error("Product {} had empty values which were filled with standards, please check them!!!", sku);
                return this;
            }
        }

        private boolean jsonSuccessfullyParsed(JsonObject json) {
            this.magentoQuantity = json.get("extension_attributes")
                    .getAsJsonObject().get("stock_item")
                    .getAsJsonObject().get("qty").getAsInt();

            for (JsonElement element : json.get("custom_attributes").getAsJsonArray()) {
                switch (element.getAsJsonObject().get("attribute_code").getAsString()) {
                    case "amazon_qty":
                        this.amazonQuantity = element.getAsJsonObject().get("value").getAsInt();
                        break;
                    case "delivery":
                        this.amazonDelivery = element.getAsJsonObject().get("value").getAsInt();
                        break;
                    case "delivery_wenn_na":
                        this.deliveryStandard = element.getAsJsonObject().get("value").getAsInt();
                        break;
                }
                if (amazonQuantity != null && amazonDelivery != null && deliveryStandard != null) {
                    return true;
                }
            }
            if (amazonQuantity == null) {
                this.amazonQuantity = this.magentoQuantity;
            }
            if (amazonDelivery == null) {
                this.amazonDelivery = this.deliveryStandard;
            }
            return false;
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
