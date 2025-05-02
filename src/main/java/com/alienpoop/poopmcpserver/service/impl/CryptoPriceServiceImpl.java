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
          "Queries the current price, market capitalization, 24-hour trading volume, and 24-hour change of a specified cryptocurrency. The 'coin' parameter should be the full name of the cryptocurrency (e.g., 'moss', 'bitcoin'), extracted directly from user input without spelling correction or truncation.")
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
