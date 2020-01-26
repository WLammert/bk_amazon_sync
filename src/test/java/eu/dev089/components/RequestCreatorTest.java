package eu.dev089.components;

import static eu.dev089.FeatureMatcher.createFeatureMatcher;
import static eu.dev089.JsonTemplates.getValidProduct;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import okhttp3.Request;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RequestCreatorTest {

    private final RequestCreator creator = new RequestCreator("token");

    @Test
    void setsBearerToken() {
        var token = "THIS_IS_MY_TOKEN";
        var creator = new RequestCreator(token);
        var request = creator.requestBuilder("somePath").get().build();

        assertThat(request, hasToken(token));
    }

    private static Matcher<Request> hasToken(String token) {
        return createFeatureMatcher(hasItem("Bearer " + token), "token",
                r -> r.headers().values("Authorization"));
    }

    @ParameterizedTest
    @MethodSource("emptyTokens")
    void reportsNoTokenSet(String token) {
        assertThrows(IllegalArgumentException.class, () -> {
            new RequestCreator(token);
        });
    }

    static Stream<String> emptyTokens() {
        return Stream.of(null, "");
    }

    @ParameterizedTest
    @MethodSource("headers")
    void setsHeaders(String header) {
        var builder = creator.requestBuilder("context");

        assertThat(builder, hasHeaderProperty(header));
    }

    @Test
    void createsAmazonRequest() {
        var query = creator.getAmazonCategoryProducts(1).url().query();

        assertThat(query, allOf(
                containsString("searchCriteria[filterGroups][0][filters][0][field]=category_id"),
                containsString("searchCriteria[filterGroups][0][filters][0][value]=2927"),
                containsString("searchCriteria[filterGroups][0][filters][0][conditionType]=eq"),
                containsString("searchCriteria[filterGroups][0][filters][0][conditionType]=eq"),
                containsString("fields=items[sku,name]&searchCriteria[current_page]=1")
        ));
    }

    @Test
    void createsUpdateRequest() {
        var product = getValidProduct("my_product_sku");
        var request = creator.updateProductRequest(product);

        assertThat(request, allOf(
                methodIs("PUT"),
                urlContains("V1/products/" + product.getSku())
        ));
    }

    @Test
    void returnsProductDataForSku() {
        var sku = "MY_SKU";
        var request = creator.getProduktDataForSku(sku);

        assertThat(request, allOf(
                methodIs("GET"),
                urlContains("V1/products/" + sku),
                urlContains("fields=sku,name,extension_attributes[stock_item[qty]],custom_attributes")
        ));
    }

    private static Matcher<Request> methodIs(String method) {
        return createFeatureMatcher(is(method), "method", Request::method);
    }

    private static Matcher<Request> urlContains(String value) {
        return createFeatureMatcher(containsString(value), "url",
                req -> req.url().toString());
    }

    private static Matcher<Request.Builder> hasHeaderProperty(String header) {
        return createFeatureMatcher(is(notNullValue()), "token", r -> r.build().headers().values(header));
    }

    static Stream<String> headers() {
        return Stream.of("Authorization", "Cache-Control", "Connection");
    }
}