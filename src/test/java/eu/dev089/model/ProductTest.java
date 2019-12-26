package eu.dev089.model;

import static eu.dev089.FeatureMatcher.createFeatureMatcher;
import static eu.dev089.JsonTemplates.getWithAmazonQuantity;
import static eu.dev089.JsonTemplates.getWithDelivery;
import static eu.dev089.JsonTemplates.getWithDeliveryWhenNa;
import static eu.dev089.JsonTemplates.getWithQuantity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.gson.JsonObject;
import eu.dev089.components.Updater;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    @Test
    void checksCorrectQuantity() {
        var quantity = Product.builder().parseQuantity(getWithQuantity("3"));

        assertThat(quantity, is(3));
    }

    @Test
    void handlesInvalidQuantity() {
        var quantity = Product.builder().parseQuantity(getWithQuantity("_"));

        assertThat(quantity, is(0));
    }

    @ParameterizedTest
    @MethodSource("validFields")
    void succesfullyParsesField(JsonObject jsonObject, Matcher<Product> matcher) {
        var product = Product.builder().parseJson(jsonObject).build();

        assertThat(product, matcher);
    }

    static Stream<Arguments> validFields() {
        return Stream.of(
                arguments(getWithDeliveryWhenNa("5"), deliveryStandardIs(5)),
                arguments(getWithDelivery("6"), amazonDeliveryIs(6)),
                arguments(getWithAmazonQuantity("3"), amazonQuantityIs(3))
        );
    }

    private static Matcher<Product> amazonDeliveryIs(int delivery){
        return createFeatureMatcher(CoreMatchers.is(delivery), "amazonDelivery", Product::getAmazonDelivery);
    }

    private static Matcher<Product> deliveryStandardIs(int deliveryStandard){
        return createFeatureMatcher(CoreMatchers.is(deliveryStandard), "deliveryWhenNa", Product::getDeliveryStandard);
    }

    private static Matcher<Product> amazonQuantityIs(int quantity){
        return createFeatureMatcher(CoreMatchers.is(quantity), "amazonQuantity", Product::getAmazonQuantity);
    }


    @ParameterizedTest
    @MethodSource("invalidFields")
    void printsWarning(JsonObject json, String loggedString) {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Product.builder().parseJson(json);

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

    @Disabled
    @Test
    void runUpdater() {
        var updater = new Updater();
        updater.run();
    }

}

