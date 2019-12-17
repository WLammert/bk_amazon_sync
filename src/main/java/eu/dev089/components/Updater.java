package eu.dev089.components;

import static eu.dev089.model.Product.STANDARD_AMAZON_QTY;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.dev089.model.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Updater extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(Updater.class);
    private final RequestCreator creator;
    private ConnectionPool pool = new ConnectionPool(10, 30, TimeUnit.SECONDS);
    private OkHttpClient client;

    public Updater() {
        this.creator = new RequestCreator();

    }

    @Override
    public void run() {
        this.client = new Builder()
                .connectionPool(pool)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();
        var previousLastsku = "";
        var process = true;
        var page = 1;
        var productsToUpdate = new ArrayList<Product>();
        while (process) {
            var jsonObject = readResponse(creator.getAmazonCategoryProducts(page));
            if (jsonObject.isPresent()) {
                var items = jsonObject.get().get("items").getAsJsonArray();
                var skus = new ArrayList<String>();
                for (JsonElement item : items) {
                    String sku = item.getAsJsonObject().get("sku").getAsString();
                    if (!sku.contains("/")) {
                        skus.add(sku);
                    }
                }
                var lastSku = skus.get(skus.size() - 1);
                if (!previousLastsku.equals(lastSku)) {
                    previousLastsku = lastSku;
                    for (String sku : skus) {
                        Optional<Product> product = processSku(sku);
                        product.ifPresent(productsToUpdate::add);
                    }
                    page = page + 1;
                } else {
                    process = false;
                }
            } else {
                process = false;
            }
        }
        updateProducts(productsToUpdate);
        client.dispatcher().cancelAll();
        client.connectionPool().evictAll();
    }

    private void updateProducts(List<Product> updatableProducts) {
        if (!updatableProducts.isEmpty()) {
            for (var product : updatableProducts) {
                var request = creator.updateProductRequest(product);
                var response = readResponse(request);
                if (response.isPresent()) {
                    if (product.getAmazonQuantity() == STANDARD_AMAZON_QTY) {
                        LOGGER.info("Successfully updated sold out product {}, amazonQuantity:{}, amazonDelivery:{}",
                                product.getSku(), product.getAmazonQuantity(), product.getAmazonDelivery());
                    } else {
                        LOGGER.info("Successfully updated restocked product {}, amazonQuantity:{}, amazonDelivery:{}",
                                product.getSku(), product.getAmazonQuantity(), product.getAmazonDelivery());
                    }
                } else {
                    LOGGER.error("could not process request - pleaze check: {}", request);
                }
            }
        }
    }

    private Optional<Product> processSku(String sku) {
        var jsonObject = readResponse(creator.getProduktDataForSku(sku));
        if (jsonObject.isPresent()) {
            var product = Product.builder()
                    .setSku(sku)
                    .setJson(jsonObject.get())
                    .build();
            if (product.isUpdatable()) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    private Optional<JsonObject> readResponse(Request request) {
        Optional<JsonObject> jsonObject = Optional.empty();
        String body = null;
        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                body = response.body().string();
                jsonObject = Optional.of(JsonParser.parseString(body).getAsJsonObject());
            } else {
                LOGGER.error("Bienenkorb responded with responseCode {} for request {}", response.code(),
                        request.toString());
            }
        } catch (IOException e) {
            LOGGER.error("Unparsable answer from Bienenkorb: {}", body, e);
        } catch (NullPointerException e) {
            LOGGER.error("Empty answer from Bienenkorb: {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Error returned during call to Bienenkorb: {}", e.getMessage(), e);
        }

        return Optional.of(jsonObject).orElse(Optional.empty());
    }
}