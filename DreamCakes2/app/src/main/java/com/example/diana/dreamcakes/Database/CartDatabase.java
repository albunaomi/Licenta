package com.example.diana.dreamcakes.Database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.diana.dreamcakes.Model.CartItem;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public  class CartDatabase extends SQLiteAssetHelper {

    private static final String DB_NAME="CofetarieDB.db";
    private  static final int DB_VERSION=1;
    public CartDatabase(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }

    public List<CartItem> getCartItems()
    {
        SQLiteDatabase database=getReadableDatabase();
        SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();

        String [] sqlSelect={"CakeId","CakeName","CakeImage","Price","Quantity"};
        String sqlTable="[Order]";

        queryBuilder.setTables(sqlTable);
        Cursor cursor=queryBuilder.query(database,sqlSelect,null,null,null,null,null);

        final List<CartItem> result=new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                result.add((new CartItem(cursor.getString(cursor.getColumnIndex("CakeId")),
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
    public void cleanCart(){
        SQLiteDatabase database=getReadableDatabase();
        String query=String.format("DELETE FROM [Order]" );
        database.execSQL(query);
    }
}
