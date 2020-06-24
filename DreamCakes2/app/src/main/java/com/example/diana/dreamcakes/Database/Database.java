package com.example.diana.dreamcakes.Database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.diana.dreamcakes.Common.Common;
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

    public List<CartItem> getCartItems(String uPhone)
    {
        SQLiteDatabase database=getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();

        final List<CartItem> result= new ArrayList<>();
        String [] sqlSelect={"ID","UserPhone","CakeId","CakeName","CakeImage","Price","Quantity"};
        String sqlTable="[Order]";

        queryBuilder.setTables(sqlTable);
        if(uPhone!=null) {
            Cursor cursor = queryBuilder.query(database, sqlSelect, "UserPhone=?", new String[]{uPhone}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    result.add((new CartItem(cursor.getInt(cursor.getColumnIndex("ID")),
                            cursor.getString(cursor.getColumnIndex("UserPhone")),
                            cursor.getString(cursor.getColumnIndex("CakeId")),
                            cursor.getString(cursor.getColumnIndex("CakeName")),
                            cursor.getString(cursor.getColumnIndex("CakeImage")),
                            cursor.getString(cursor.getColumnIndex("Price")),
                            cursor.getString(cursor.getColumnIndex("Quantity")))));
                } while (cursor.moveToNext());
            }
        }
            return result;

    }

    public void addItemToCart(CartItem cartItem){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("INSERT  INTO [Order](UserPhone,CakeId,CakeName,CakeImage,Price,Quantity) VALUES('%s','%s','%s','%s','%s','%s');",
                cartItem.getUserPhone(),
                cartItem.getCakeId(),
                cartItem.getCakeName(),
                cartItem.getCakeImage(),
                cartItem.getPrice(),
                cartItem.getQuantity());
        database.execSQL(query);
    }
    public void removeItemFromCart(String id,String phone){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Order] WHERE CakeId='%s' AND UserPhone='%s';",id , phone);
        database.execSQL(query);
    }
    public void cleanCart(String phone){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Order] WHERE UserPhone='%s'",phone);
        database.execSQL(query);
    }

    public int getCountCart(String phone){
        int count=0;
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("SELECT COUNT(*) FROM [Order] WHERE UserPhone='%s'",phone);
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
        String query=String.format("INSERT INTO [Favorite](UserPhone,CakeId,Name,Image,Price) VALUES('%s','%s','%s','%s','%s');",
                cake.getUserPhone(),
                cake.getCakeId(),
                cake.getCakeName(),
                cake.getCakeImage(),
                cake.getPrice());
        database.execSQL(query);
    }
    public void removeFavoriteCake(String id,String phone){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Favorite] WHERE CakeId='%s'  AND UserPhone='%s';",id,phone );
        database.execSQL(query);
    }

    public List<Favorite> getFavoriteCakes(String uPhone) {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","UserPhone","CakeId", "Name", "Image", "Price"};
        String sqlTable = "[Favorite]";

        queryBuilder.setTables(sqlTable);
        Cursor cursor = queryBuilder.query(database, sqlSelect, "UserPhone=?",new String[]{uPhone}, null, null, null);

        final List<Favorite> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add((new Favorite(cursor.getInt(cursor.getColumnIndex("ID")),
                        cursor.getString(cursor.getColumnIndex("UserPhone")),
                        cursor.getString(cursor.getColumnIndex("CakeId")),
                        cursor.getString(cursor.getColumnIndex("Name")),
                        cursor.getString(cursor.getColumnIndex("Image")),
                        cursor.getString(cursor.getColumnIndex("Price")))));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public boolean isFavoriteCake(String cakeId,String phone){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("SELECT * FROM [Favorite] WHERE CakeId='%s' AND UserPhone='%s' ; ",cakeId,phone );
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
        String query=String.format("UPDATE [Order] SET Quantity= %s WHERE  UserPhone='%s' AND ID='%s';",
                cart.getQuantity(),cart.getUserPhone(),cart.getID());
        database.execSQL(query);
    }
}
