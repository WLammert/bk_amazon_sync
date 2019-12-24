package eu.dev089.model;

import static eu.dev089.JsonTemplates.getValidProductAsJsonObject;
import static eu.dev089.JsonTemplates.getWithAmazonQuantity;
import static eu.dev089.JsonTemplates.getWithDelivery;
import static eu.dev089.JsonTemplates.getWithDeliveryWhenNa;
import static eu.dev089.JsonTemplates.getWithQuantity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.dev089.JsonTemplates;
import eu.dev089.components.Updater;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.logging.LogRecordListener;

class ProductTest {

    @Test
    void succesfullyParsesJson() {
        var result = Product.builder().jsonSuccessfullyParsed(getValidProductAsJsonObject());

        assertThat(result, is(true));
    }

    @Test
    void successfullyParsesWhenInvalidQty() {
        var json = getWithQuantity("_");

        var result = Product.builder()
                .jsonSuccessfullyParsed(json);

        assertThat(result, is(true));
    }

    @Test
    void successfullyParsesWhenInvalidAmazonQuantity() {
        var json = getWithAmazonQuantity("_");

        var result = Product.builder().jsonSuccessfullyParsed(json);

        assertThat(result, is(true));
    }

    @Test
    void successfullyParsesWhenInvalidDelivery() {
        var json = getWithDelivery("_");

        var result = Product.builder().jsonSuccessfullyParsed(json);

        assertThat(result, is(true));
    }

    @Test
    void successfullyParsesWhenInvalidDeliveryWhenNa() {
        var json = getWithDeliveryWhenNa("_");

        var result = Product.builder().jsonSuccessfullyParsed(json);

        assertThat(result, is(true));
    }

    @ParameterizedTest
    @MethodSource("invalidFields")
    void printsWarning(JsonObject json, String loggedString) {
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Product.builder().jsonSuccessfullyParsed(json);

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

