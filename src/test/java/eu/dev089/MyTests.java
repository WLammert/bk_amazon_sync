package eu.dev089;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class MyTests {

    @Disabled
    @Test
    void firstTest() {
        String schema = "type Query{hello: String} schema{query: Query}";

    }
}
