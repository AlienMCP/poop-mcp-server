package com.alienpoop.poopmcpserver.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alienpoop.poopmcpserver.annotation.ToolServer;
import com.alienpoop.poopmcpserver.service.ICryptoPriceService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@ToolServer
public class CryptoPriceServiceImpl implements ICryptoPriceService {

  @Value("${coingecko.api.key}")
  private String coingeckoApiKey;

  @Value("${coingecko.api.url}")
  private String api;

  @Override
  @Tool(
      name = "fetchCryptoPrice",
      description =
          "查询指定代币的当前价格、市场总值、24小时交易量和24小时变化。参数 'coin' 应为代币的完整名称（例如 'moss'、'bitcoin'），直接从用户输入中提取，不进行拼写校正或截断。")
  public String fetchCryptoPrice(String coin) {
    String body =
        HttpRequest.get(api + "/api/v3/coins/markets")
            .form("vs_currency", "USD")
            .form("ids", coin)
            .header("x-cg-demo-api-key", coingeckoApiKey)
            .execute()
            .body();

    JSONArray results = new JSONArray(body);

    if (results.isEmpty()) {
      return coin + " not found";
    }

    JSONObject result = (JSONObject) results.get(0);

    return result.toString();
  }
}
