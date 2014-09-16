package com.divinedube.helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Divine Dube on 2014/09/13.
 */
public class TestClass {

       public static void main(String args[]){
           String json =
                   "{\"success\":true,\"info\":\"user\",\"data\":{\"user\":{\"id\":8,\"name\":null,\"email\":\"d@m.com\",\"created_at\":\"2014-09-13T18:06:41.624Z\",\"updated_at\":\"2014-09-13T18:06:41.624Z\",\"password_digest\":null,\"remember_token\":\"JArXVlD_kwihXH2JxSBMyw\",\"meter_id\":null,\"password\":\"1234\"}}}";
           try {
               JSONObject jso = new JSONObject(json);
               System.out.println(jso.getJSONObject("data").getJSONObject("user").getInt("id"));
           }catch (JSONException e){
               e.printStackTrace();
           }
       }
}
