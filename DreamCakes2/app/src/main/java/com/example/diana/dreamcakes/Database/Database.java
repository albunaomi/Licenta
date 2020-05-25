package com.example.diana.dreamcakes.Database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.diana.dreamcakes.Model.Cake;
import com.example.diana.dreamcakes.Model.CartItem;
import com.example.diana.dreamcakes.Model.Favorite;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public  class Database extends SQLiteAssetHelper {

    private static final String DB_NAME="CofetarieDB.db";
    private  static final int DB_VERSION=1;


    public Database(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    public List<CartItem> getCartItems()
    {
        SQLiteDatabase database=getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();

        String [] sqlSelect={"ID","CakeId","CakeName","CakeImage","Price","Quantity"};
        String sqlTable="[Order]";

        queryBuilder.setTables(sqlTable);
        Cursor cursor=queryBuilder.query(database,sqlSelect,null,null,null,null,null);

        final List<CartItem> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add((new CartItem(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("CakeId")),
                        cursor.getString(cursor.getColumnIndex("CakeName")),
                        cursor.getString(cursor.getColumnIndex("CakeImage")),
                        cursor.getString(cursor.getColumnIndex("Price")),
                        cursor.getString(cursor.getColumnIndex("Quantity")))));
            }while(cursor.moveToNext());
        }
        return result;
    }

    public void addItemToCart(CartItem cartItem){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("INSERT INTO [Order](CakeId,CakeName,CakeImage,Price,Quantity) VALUES('%s','%s','%s','%s','%s');",
                cartItem.getCakeId(),
                cartItem.getCakeName(),
                cartItem.getCakeImage(),
                cartItem.getPrice(),
                cartItem.getQuantity());
        database.execSQL(query);
    }
    public void removeItemFromCart(String id){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Order] WHERE CakeId='%s';",id );
        database.execSQL(query);
    }
    public void cleanCart(){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Order]" );
        database.execSQL(query);
    }

    public int getCountCart(){
        int count=0;
        SQLiteDatabase database=getReadableDatabase();
        String query="SELECT COUNT(*) FROM [Order]";
        Cursor cursor=database.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                count=cursor.getInt(0);
            }while(cursor.moveToNext());
        }

        return count;
    }
    public void addFavoriteCake(Favorite cake){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("INSERT INTO [Favorite](CakeId,Name,Image,Price) VALUES('%s','%s','%s','%s');", cake.getCakeId(),
                cake.getCakeName(),
                cake.getCakeImage(),
                cake.getPrice());
        database.execSQL(query);
    }
    public void removeFavoriteCake(String id){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Favorite] WHERE CakeId='%s';",id );
        database.execSQL(query);
    }

    public List<Favorite> getFavoriteCakes() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"CakeId", "Name", "Image", "Price"};
        String sqlTable = "[Favorite]";

        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(database, sqlSelect, null, null, null, null, null);

        final List<Favorite> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add((new Favorite(cursor.getString(cursor.getColumnIndex("CakeId")),
                        cursor.getString(cursor.getColumnIndex("Name")),
                        cursor.getString(cursor.getColumnIndex("Image")),
                        cursor.getString(cursor.getColumnIndex("Price")))));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public boolean isFavoriteCake(String id){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("SELECT * FROM [Favorite] WHERE CakeId='%s';",id );
        Cursor cursor=database.rawQuery(query,null);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return  true;
    }

    public void updateCart(CartItem cart) {
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("UPDATE [Order] SET Quantity= %s WHERE ID=%d;",cart.getQuantity(),cart.getID());
        database.execSQL(query);
    }
}
