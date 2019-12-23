package eu.dev089.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.dev089.components.Updater;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void succesfullyParsesJson() {
        var result = Product.builder().jsonSuccessfullyParsed(getProductJson());

        assertThat(result, is(true));
    }

    @Test
    void unsuccesfullyParsesJson() {
        var result = Product.builder().jsonSuccessfullyParsed(JsonParser.parseString("some invalid json").getAsJsonObject());

        assertThat(result, is(false));
    }



    @Disabled
    @Test
    void runUpdater() {
        var updater = new Updater();
        updater.run();
    }

    JsonObject getProductJson() {
        return JsonParser.parseString(ExampleJsonProduct.JSON).getAsJsonObject();
    }
}

class ExampleJsonProduct {

    public static final String JSON = "{\"sku\":\"007-\",\"name\":\"Produktname\",\"extension_attributes\":"
            + "{\"stock_item\":{\"qty\":10}},\"custom_attributes\":[{\"attribute_code\":\"description\",\"value\":\"Produkt description\"},"
            + "{\"attribute_code\":\"short_description\",\"value\":\"shortdescription\"},{\"attribute_code\":\"manufacturer\",\"value\":\"17\"},"
            + "{\"attribute_code\":\"meta_title\",\"value\":\"someMetaTitle\"},{\"attribute_code\":\"meta_keyword\",\"value\":\"some meta keword\"},"
            + "{\"attribute_code\":\"meta_description\",\"value\":\"some meta description\"},"
            + "{\"attribute_code\":\"image\",\"value\":\"some image\"},{\"attribute_code\":\"small_image\",\"value\":\"some small image\"},"
            + "{\"attribute_code\":\"thumbnail\",\"value\":\"some thumbnail\"},{\"attribute_code\":\"color\",\"value\":\"281\"},"
            + "{\"attribute_code\":\"custom_design\",\"value\":\"0\"},{\"attribute_code\":\"page_layout\",\"value\":\"0\"},"
            + "{\"attribute_code\":\"category_ids\",\"value\":[\"2830\",\"2841\",\"2927\"]},{\"attribute_code\":\"options_container\",\"value\":\"0\"},"
            + "{\"attribute_code\":\"required_options\",\"value\":\"0\"},{\"attribute_code\":\"has_options\",\"value\":\"0\"},"
            + "{\"attribute_code\":\"country_of_manufacture\",\"value\":\"DE\"},{\"attribute_code\":\"url_key\",\"value\":\"pomp-bank-bordeauxrot\"},"
            + "{\"attribute_code\":\"gift_message_available\",\"value\":\"0\"},{\"attribute_code\":\"tax_class_id\",\"value\":\"20\"},"
            + "{\"attribute_code\":\"custom_stock_status_qty_based\",\"value\":\"1\"},{\"attribute_code\":\"custom_stock_status_qty_rule\",\"value\":\"1051\"},"
            + "{\"attribute_code\":\"sw_featured\",\"value\":\"0\"},"
            + "{\"attribute_code\":\"amazon_qty\",\"value\":\"10\"},"
            + "{\"attribute_code\":\"asin\",\"value\":\"B00D2V05V8\"},"
            + "{\"attribute_code\":\"delivery\",\"value\":\"10\"},"
            + "{\"attribute_code\":\"featured\",\"value\":\"0\"},"
            + "{\"attribute_code\":\"lieferzeit\",\"value\":\"5\"},"
            + "{\"attribute_code\":\"is_recurring\",\"value\":\"0\"},{\"attribute_code\":\"hide_default_stock_status\",\"value\":\"1\"},"
            + "{\"attribute_code\":\"disable_amazonpayments\",\"value\":\"0\"},{\"attribute_code\":\"delivery_wenn_na\",\"value\":\"10\"}]}";
}

