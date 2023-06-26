package com.sdt.trproject.ksh.train;

import com.sdt.trproject.ksh.BoardVo;
import com.sdt.trproject.ksh.ResponseVo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TrainService {
    @POST("/train/charge")
    Call<ResponseVo> getCharge(@Body BoardVo index);
}
