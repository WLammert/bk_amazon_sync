package eu.dev089.model;

import static eu.dev089.FeatureMatcher.createFeatureMatcher;
import static eu.dev089.JsonTemplates.getValidProductAsJsonObject;
import static eu.dev089.JsonTemplates.getWithAmazonQuantity;
import static eu.dev089.JsonTemplates.getWithDelivery;
import static eu.dev089.JsonTemplates.getWithDeliveryWhenNa;
import static eu.dev089.JsonTemplates.getWithQuantity;
import static eu.dev089.model.Product.STANDARD_AMAZON_QTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.dev089.model.Product.ProductBuilder;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    private final ProductBuilder builder = Product.builder();

    @Test
    void checksCorrectQuantity() {
        var quantity = builder.parseQuantity(getWithQuantity("3"));

        assertThat(quantity, is(3));
    }

    @Test
    void handlesInvalidQuantity() {
        var quantity = builder.parseQuantity(getWithQuantity("_"));

        assertThat(quantity, is(0));
    }

    @ParameterizedTest
    @MethodSource("validFields")
    void succesfullyParsesField(JsonObject jsonObject, Matcher<Product> matcher) {
        var product = builder.parseJson(jsonObject).build();

        assertThat(product, matcher);
    }

    static Stream<Arguments> validFields() {
        return Stream.of(
                arguments(getWithDeliveryWhenNa("5"), deliveryStandardIs(5)),
                arguments(getWithDelivery("6"), amazonDeliveryIs(6)),
                arguments(getWithAmazonQuantity("3"), amazonQuantityIs(3)),
                arguments(getWithQuantity("7"), magentoQuantityIs(7))
        );
    }

    private static Matcher<Product> amazonDeliveryIs(int delivery) {
        return createFeatureMatcher(CoreMatchers.is(delivery), "amazonDelivery", Product::getAmazonDelivery);
    }

    private static Matcher<Product> deliveryStandardIs(int deliveryStandard) {
        return createFeatureMatcher(CoreMatchers.is(deliveryStandard), "deliveryWhenNa", Product::getDeliveryStandard);
    }

    private static Matcher<Product> amazonQuantityIs(int quantity) {
        return createFeatureMatcher(CoreMatchers.is(quantity), "amazonQuantity", Product::getAmazonQuantity);
    }

    private static Matcher<Product> magentoQuantityIs(int quantity) {
        return createFeatureMatcher(CoreMatchers.is(quantity), "magentoQuantity", Product::getMagentoQuantity);
    }

    private static Matcher<Product> skuIs(String sku) {
        return createFeatureMatcher(CoreMatchers.is(sku), "sku", Product::getSku);
    }

    @Test
    void handlesNullValues() {
        var json = "{\"attribute_code\":\"amazon_qty\"}";
        var element = JsonParser.parseString(json).getAsJsonObject();

        var result = builder.getValueAsInteger(element, "amazon_qty");

        assertThat(result, is(0));
    }

    @Test
    void handlesNullDeliveryWhenNaToStandard() {
        var json = "{\"attribute_code\":\"delivery_wenn_na\"}";
        var element = JsonParser.parseString(json).getAsJsonObject();

        var result = builder.getValueAsInteger(element, "delivery_wenn_na");

        assertThat(result, is(Product.STANDARD_DELIVERY_WHEN_NA));
    }


    @ParameterizedTest
    @MethodSource("invalidFields")
    void printsWarning(JsonObject json, String loggedString) {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        builder.parseJson(json);

        assertThat(outContent.toString(), containsString(loggedString));
    }

    static Stream<Arguments> invalidFields() {
        return Stream.of(
                arguments(getWithDeliveryWhenNa("_"), "Nichtnumerischer Wert im Feld delivery_wenn_na"),
                arguments(getWithDelivery("_"), "Nichtnumerischer Wert im Feld delivery"),
                arguments(getWithAmazonQuantity("_"), "Nichtnumerischer Wert im Feld amazon_qty"),
                arguments(getWithQuantity("_"), "had no valid quantity, set to 0")
        );
    }

    @Test
    void setsQuantityWhenSoldout() {
        var product = Product.builder()
                .setSku("007")
                .setMagentoQuantity(0)
                .setAmazonQuantity(10)
                .setDeliveryStandard(10)
                .setAmazonDelivery(0)
                .build();

        assertThat(product, allOf(
                needsUpdate(true),
                amazonQuantityIs(STANDARD_AMAZON_QTY)
        ));
    }

    @Test
    void setsDeliveryWhenSoldout() {
        var product = Product.builder()
                .setSku("007")
                .setMagentoQuantity(0)
                .setAmazonQuantity(10)
                .setDeliveryStandard(10)
                .setAmazonDelivery(0)
                .build();

        assertThat(product, allOf(
                needsUpdate(true),
                amazonDeliveryIs(10)
        ));
    }

    @Test
    void correctsAmazonQuantity() {
        var product = Product.builder()
                .setSku("007")
                .setMagentoQuantity(10)
                .setAmazonQuantity(9)
                .setDeliveryStandard(10)
                .setAmazonDelivery(0)
                .build();

        assertThat(product, allOf(
                needsUpdate(true),
                amazonQuantityIs(10)
        ));
    }

    @Test
    void correctsDeliveryWhenRestocked() {
        var product = Product.builder()
                .setSku("007")
                .setMagentoQuantity(10)
                .setAmazonQuantity(33)
                .setDeliveryStandard(10)
                .setAmazonDelivery(10)
                .build();

        assertThat(product, allOf(
                needsUpdate(true),
                amazonDeliveryIs(0)
        ));
    }

    @Test
    void noNeedForUpdate() {
        var product = Product.builder()
                .setSku("007")
                .setMagentoQuantity(10)
                .setAmazonQuantity(10)
                .setDeliveryStandard(10)
                .setAmazonDelivery(0)
                .build();

        assertThat(product, needsUpdate(false));
    }

    private static Matcher<Product> needsUpdate(boolean update) {
        return createFeatureMatcher(CoreMatchers.is(update), "update", Product::isUpdatable);
    }

    @Test
    void correctSku() {
        var product = builder.parseJson(getValidProductAsJsonObject()).setSku("mySku").build();

        assertThat(product, skuIs("mySku"));
    }

    @Test
    void fieldsInToString() {
        var product = builder.parseJson(getValidProductAsJsonObject()).build();

        assertThat(product.toString(), allOf(
                containsStringIgnoringCase("sku"),
                containsStringIgnoringCase("magentoQuantity"),
                containsStringIgnoringCase("deliveryStandard"),
                containsStringIgnoringCase("amazonQuantity"),
                containsStringIgnoringCase("amazonDelivery")
        ));
    }
}


