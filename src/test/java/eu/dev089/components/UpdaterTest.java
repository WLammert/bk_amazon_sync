package eu.dev089.components;

import eu.dev089.model.Product;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UpdaterTest {

    private Updater updater = new Updater("MYTOKEN");

    @Test
    void readsResponse() {
        Optional<Product> mysku = updater.processSku("mysku");
    }
}