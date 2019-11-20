package eu.dev089.components;

import eu.dev089.model.Product;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestCreator {

    private static final String URL = "https://www.bienenkorb-shop.de/index.php/rest/V1/";

    private Request.Builder requestBuilder(String contextPath) {
        var bk_token = System.getenv().get("BK_TOKEN");
        if (bk_token == null || bk_token.isEmpty()) {
            throw new IllegalArgumentException("please provide a bearer token via ENV");
        }
        return new Request.Builder()
                .url(URL + contextPath)
                .addHeader("Authorization", String.format("Bearer %s", bk_token))
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive");
    }

    public Request getActiveProductSkus() {
        var query = "searchCriteria[filter_groups][0][filters][0][field]=status&searchCriteria[filter_groups][0][filters][0][value]=1&searchCriteria[filter_groups][0][filters][0][condition_type]=finset&fields=items[sku]";
        return requestBuilder("products" + "?" + query).get().build();
    }

    public Request get100SkuForPage(int page) {
        var query =
                "searchCriteria[filter_groups][0][filters][0][field]=status&searchCriteria[filter_groups][0][filters][0][value]=1&searchCriteria[filter_groups][0][filters][0][condition_type]=finset&fields=items[sku]&searchCriteria[current_page]="
                        + String.valueOf(page) + "&searchCriteria[page_size]=500";
        return requestBuilder("products" + "?" + query).get().build();
    }

    public Request getAmazonCategoryProducts() {
        var query = "searchCriteria[filterGroups][0][filters][0][field]=category_id& searchCriteria[filterGroups][0][filters][0][value]=2927&searchCriteria[filterGroups][0][filters][0][conditionType]=eq&fields=items[sku,name]";
        return requestBuilder("products" + "?" + query).get().build();
    }

    public Request getAmazonCategoryProducts(int page) {
        var query =
                "searchCriteria[filterGroups][0][filters][0][field]=category_id& searchCriteria[filterGroups][0][filters][0][value]=2927&searchCriteria[filterGroups][0][filters][0][conditionType]=eq&fields=items[sku,name]&searchCriteria[current_page]="
                        + page + "&searchCriteria[page_size]=200";
        return requestBuilder("products" + "?" + query).get().build();
    }

    public Request getFirst100ForTesting() {
        var request =
                "products?searchCriteria[filterGroups][0][filters][0][field]=category_id& searchCriteria[filterGroups][0][filters][0][value]=2927&searchCriteria[filterGroups][0][filters][0][conditionType]=eq&fields=items[sku,name]&searchCriteria[current_page]=1&searchCriteria[page_size]=100";
        return requestBuilder(request).get().build();
    }

    public Request updateProductRequest(Product product) {
        var anotherJson = String.format("{\n"
                + "  \"product\": {\n"
                + "  \t\"sku\": \"%s\",\n"
                + "    \"custom_attributes\": [\n"
                + "      {\n"
                + "        \"attribute_code\": \"amazon_qty\",\n"
                + "        \"value\": \"%d\"\n"
                + "      },{\n"
                + "        \"attribute_code\": \"delivery\",\n"
                + "        \"value\": \"%d\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"saveOptions\": true\n"
                + "}", product.getSku(), product.getAmazonQuantity(), product.getAmazonDelivery());

        var body = RequestBody.create(anotherJson, MediaType.parse("application/json"));
        return requestBuilder(String.format("products/%s/", product.getSku())).put(body).build();
    }

    public Request getProduktDataForSku(String sku) {
        var query = "?fields=sku,name,extension_attributes[stock_item[qty]],custom_attributes";
        return requestBuilder(String.format("products/%s/%s", sku, query)).get().build();
    }
}

