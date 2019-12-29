package eu.dev089.components;

import static eu.dev089.FeatureMatcher.createFeatureMatcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import okhttp3.Request;
import okhttp3.Request.Builder;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class RequestCreatorTest {

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
        var creator = new RequestCreator("token");
        var builder = creator.requestBuilder("context");

        assertThat(builder, hasHeaderProperty(header));
    }

    private static Matcher<Request.Builder> hasHeaderProperty(String header) {
        return createFeatureMatcher(is(notNullValue()), "token", r -> r.build().headers().values(header));
    }

    static Stream<String> headers() {
        return Stream.of("Authorization", "Cache-Control", "Connection");
    }
}