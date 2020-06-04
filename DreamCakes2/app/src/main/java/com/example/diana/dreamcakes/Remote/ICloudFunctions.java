package com.example.diana.dreamcakes.Remote;

import com.example.diana.dreamcakes.Model.BraintreeToken;
import com.example.diana.dreamcakes.Model.BraintreeTransaction;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;



public interface ICloudFunctions {
    @GET("token")
    Observable<BraintreeToken> getToken();

    @POST("checkout")
    Observable<BraintreeTransaction> submitPayment(@Field("amount") double amount,
                                                   @Field("payment_method_nonce")String nonce);
}

