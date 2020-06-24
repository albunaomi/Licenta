package com.example.diana.dreamcakes.Remote;

import com.example.diana.dreamcakes.Model.DataMessage;
import com.example.diana.dreamcakes.Model.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAEwysc6M:APA91bHnA_zjE_Mwfx4ioBhDs54uH8TChmS0Z8VolB2zcCLhqiRFmOb3T4NozfJ_9unm0PMRuBy8SjwgbLlmHFwIXeXmziCeJtj1VBUB0zlU-cRmy8H_FBScZZW6xUlTWQfMLxNdbDeH"
    }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body DataMessage body);
}
