package com.alienpoop.poopmcpserver.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alienpoop.poopmcpserver.annotation.ToolServer;
import com.alienpoop.poopmcpserver.service.ICryptoPriceService;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ToolServer
public class CryptoPriceServiceImpl implements ICryptoPriceService {

  @Value("${coingecko.api.key}")
  private String coingeckoApiKey;

  @Value("${coingecko.api.url}")
  private String api;

  @Value("${hyperAGI.api}")
  private String hyperAGIAPI;

  @Override
  @Tool(
      name = "fetchCryptoPrice",
      description =
          "Queries the current price, market capitalization, 24-hour trading volume, and 24-hour change of a specified cryptocurrency. The ‘coin’ parameter should be the official full name of the cryptocurrency (e.g., ‘moss’, ‘bitcoin’) and should be extracted directly from user input. If the user provides a name in Chinese (e.g., “比特币”) or an abbreviation, it should be translated or mapped to the corresponding official English name, such as ‘bitcoin’ or the appropriate token name.")
  public String fetchCryptoPrice(String coin) {

    log.info("Fetching crypto price for: " + coin);

    String coinId = getCrypto(coin);

    if (coinId == null) {
      return coin + " not found";
    }

    log.info("Crypto ID: " + coinId);

    String body =
        HttpRequest.get(api + "/api/v3/coins/markets")
            .form("vs_currency", "USD")
            .form("ids", coinId)
            .header("x-cg-demo-api-key", coingeckoApiKey)
            .execute()
            .body();

    log.info("markets query body:{} ", body);

    JSONArray results = new JSONArray(body);

    if (results.isEmpty()) {
      return coinId + " not found";
    }

    return results.toString();
  }

  private String getCrypto(String token) {

    String body =
        HttpRequest.get(hyperAGIAPI + "/mgn/cryptoCurrency/list")
            .form("keyword", token)
            .form("pageSize", 10)
            .timeout(10000)
            .execute()
            .body();

    JSONObject result = new JSONObject(body);

    JSONArray records = result.getJSONObject("result").getJSONArray("records");

    if (records.isEmpty()) {
      return null;
    }

    return records.stream()
        .map(i -> ((JSONObject) i).getStr("currencyId"))
        .collect(Collectors.joining(","));
  }
}
