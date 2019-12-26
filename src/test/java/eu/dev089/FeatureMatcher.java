package eu.dev089;

import java.util.function.Function;
import org.hamcrest.Matcher;

public final class FeatureMatcher {

    private FeatureMatcher() {
    }

    /**
     * Creates a matcher that extracts a object property via a lambda expression and matches the property against
     * another matcher.
     *
     * @param matcher the matcher that will be applied on the extracted property
     * @param description the description of the property that will be used in the test description
     * @param func the function to extract the object property
     * @param <T> the type of the property
     * @param <U> the type of the object
     * @return The matcher.
     */
    public static <T, U> Matcher<U> createFeatureMatcher(Matcher<T> matcher, String description, Function<U, T> func) {
        return new org.hamcrest.FeatureMatcher<U, T>(matcher, description, description) {
            @Override
            protected T featureValueOf(final U actual) {
                return func.apply(actual);
            }
        };
    }
}
