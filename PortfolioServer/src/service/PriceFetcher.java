package service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class PriceFetcher {
    private static final ConcurrentHashMap<String, CachedPrice> priceCache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(5);
    
    private static class CachedPrice {
        final double price;
        final long timestamp;
        
        CachedPrice(double price) {
            this.price = price;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_DURATION;
        }
    }

    public static double getLivePrice(String symbol, String type) {
        String cacheKey = symbol + ":" + type;
        CachedPrice cached = priceCache.get(cacheKey);
        
        // Return cached price if available and not expired
        if (cached != null && !cached.isExpired()) {
            return cached.price;
        }
        
        try {
            double price = fetchPriceFromAPI(symbol, type);
            priceCache.put(cacheKey, new CachedPrice(price));
            return price;
        } catch (Exception e) {
            // Return cached price even if expired if API fails
            if (cached != null) {
                return cached.price;
            }
            System.err.println("Failed to fetch price for: " + symbol);
            return 0.0;
        }
    }

    private static double fetchPriceFromAPI(String symbol, String type) throws Exception {
        if ("crypto".equalsIgnoreCase(type)) {
            String apiUrl = "https://api.coingecko.com/api/v3/simple/price?ids=" + 
                           symbol.toLowerCase() + "&vs_currencies=usd";
            
            // Add delay to prevent rate limiting
            Thread.sleep(1000); // 1 second between API calls
            
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStream response = conn.getInputStream();
                 Scanner scanner = new Scanner(response).useDelimiter("\\A")) {
                String json = scanner.hasNext() ? scanner.next() : "{}";
                JSONObject obj = new JSONObject(json);
                return obj.getJSONObject(symbol.toLowerCase()).getDouble("usd");
            }
        } else {
            String apiUrl = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + 
                          symbol.toUpperCase();
            
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (InputStream response = conn.getInputStream();
                 Scanner scanner = new Scanner(response).useDelimiter("\\A")) {
                String json = scanner.hasNext() ? scanner.next() : "{}";
                JSONObject obj = new JSONObject(json);
                return obj.getJSONObject("quoteResponse")
                         .getJSONArray("result")
                         .getJSONObject(0)
                         .getDouble("regularMarketPrice");
            }
        }
    }
}