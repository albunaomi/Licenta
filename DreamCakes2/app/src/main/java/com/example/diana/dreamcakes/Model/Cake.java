package com.example.diana.dreamcakes.Model;

/**
 * Created by Diana on 5/17/2020.
 */

public class Cake {
   private String Name;
   private String Image;
   private String Description;
   private String Price;
   private String CategoryId;

   public Cake(String name, String image, String description, String price, String categoryId) {
      Name = name;
      Image = image;
      Description = description;
      Price = price;
      CategoryId = categoryId;
   }

   public Cake() {
   }

   public String getName() {
      return Name;
   }

   public void setName(String name) {
      Name = name;
   }

   public String getImage() {
      return Image;
   }

   public void setImage(String image) {
      Image = image;
   }

   public String getDescription() {
      return Description;
   }

   public void setDescription(String description) {
      Description = description;
   }

   public String getPrice() {
      return Price;
   }

   public void setPrice(String price) {
      Price = price;
   }

   public String getCategoryId() {
      return CategoryId;
   }

   public void setCategoryId(String categoryId) {
      CategoryId = categoryId;
   }
}
