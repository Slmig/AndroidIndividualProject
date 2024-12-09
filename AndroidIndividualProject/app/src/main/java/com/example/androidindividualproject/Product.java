package com.example.androidindividualproject;

public class Product {
    public String Title;
    public String Description;
    public int Price;
    public int RecoveryValue;
    public ProductTypeEnum ProductType;
    public Product(String title, String description, int price,int recoveryValue, ProductTypeEnum productType){
        Title = title;
        Description = description;
        Price = price;
        RecoveryValue = recoveryValue;
        ProductType = productType;
    }
    public enum ProductTypeEnum {
        Food,
        RelaxProcedure
    }
}
