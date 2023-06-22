package com.sdt.trproject.ksh;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ResponseVo {
   @SerializedName("result")
      @Expose
      private String result;
   @SerializedName("message")
   @Expose
   private String message;
   @SerializedName("data")
   @Expose
   private List<BoardVo> dataList;



   public ResponseVo(String result, List<BoardVo> dataList) {
      this.result = result;
      this.dataList = dataList;
   }

   public ResponseVo(String result, String message, List<BoardVo> dataList) {
      this.result = result;
      this.message = message;
      this.dataList = dataList;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getResult() {
      return result;
   }

   public void setResult(String result) {
      this.result = result;
   }

   public List<BoardVo> getDataList() {
      return dataList;
   }

   public void setDataList(List<BoardVo> dataList) {
      this.dataList = dataList;
   }
}
