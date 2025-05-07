package com.alienpoop.poopmcpserver.dto;

import cn.hutool.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Command {
  private String intent;
  private Map<String, Object> parameters = new HashMap<>();
  private Map<String, Object> context = new HashMap<>();



  public String toJsonString() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.set("intent", intent);
    jsonObject.set("parameters", parameters);
    jsonObject.set("context", context);
    return jsonObject.toString();
  }
}
