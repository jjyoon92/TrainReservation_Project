package com.sdt.trproject.ksh;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface BoardService {


    @Headers(value = {
        "accept: application/json" ,
        "content-type: application/json"
    })

    @POST("/board/select")
    Call<ResponseVo> get_board() ;

    @POST("/board/select/search")
    Call<ResponseVo> get_board_search(@Body BoardVo search);


    @POST("/board/select/index")
    Call<ResponseVo> get_board_index(@Body BoardVo index);

}
