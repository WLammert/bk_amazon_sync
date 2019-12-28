package eu.dev089;

import static eu.dev089.FeatureMatcher.createFeatureMatcher;

import eu.dev089.model.Product;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

public class ProductMatchers {

    public static Matcher<Product> amazonDeliveryIs(int delivery) {
        return createFeatureMatcher(CoreMatchers.is(delivery), "amazonDelivery", Product::getAmazonDelivery);
    }

    public static Matcher<Product> deliveryStandardIs(int deliveryStandard) {
        return createFeatureMatcher(CoreMatchers.is(deliveryStandard), "deliveryWhenNa", Product::getDeliveryStandard);
    }

    public static Matcher<Product> amazonQuantityIs(int quantity) {
        return createFeatureMatcher(CoreMatchers.is(quantity), "amazonQuantity", Product::getAmazonQuantity);
    }

    public static Matcher<Product> magentoQuantityIs(int quantity) {
        return createFeatureMatcher(CoreMatchers.is(quantity), "magentoQuantity", Product::getMagentoQuantity);
    }

    public static Matcher<Product> skuIs(String sku) {
        return createFeatureMatcher(CoreMatchers.is(sku), "sku", Product::getSku);
    }

    public static Matcher<Product> needsUpdate(boolean update) {
        return createFeatureMatcher(CoreMatchers.is(update), "update", Product::isUpdatable);
    }
}
