package com.example.caloriecounter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class APIQuery extends Thread{

    private Product selectedProduct;
    private String barcode;
    private boolean running;

    public APIQuery(String barcode){
            this.barcode = barcode;
            selectedProduct = new Product();
            running = false;
    }

    public void run(){
        try {
            running = true;
            URLConnection connection = new URL("https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            String value = content.toString();
            br.close();
            JSONObject json = new JSONObject(value).getJSONObject("product");
            selectedProduct.setID(barcode);
            selectedProduct.setName(json.getString("product_name"));
            JSONObject nutriments = json.getJSONObject("nutriments");
            selectedProduct.setCalories(nutriments.getInt("energy-kcal_100g"));
            selectedProduct.setProtein(nutriments.getDouble("proteins_100g"));
            selectedProduct.setFat(nutriments.getDouble("fat_100g"));
            selectedProduct.setCarbs(nutriments.getDouble("carbohydrates_100g"));
            selectedProduct.setPortionUnit(nutriments.getString(""));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }finally{
            running = false;
        }
    }

    public Product getProduct(){
        if(!running)
            return selectedProduct;
        return null;
    }

}
