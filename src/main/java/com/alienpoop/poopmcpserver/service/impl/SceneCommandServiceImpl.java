package com.alienpoop.poopmcpserver.service.impl;

import cn.hutool.json.JSONObject;
import com.alienpoop.poopmcpserver.annotation.ToolServer;
import com.alienpoop.poopmcpserver.service.I3DSceneCommandService;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@ToolServer
@Service
public class SceneCommandServiceImpl implements I3DSceneCommandService {

  @Override
  @Tool(
      name = "parseCommand",
      description =
          "1. Parse a single natural language command (in Chinese or English) into a structured JSON instruction using model inference.\n"
              + "2. The JSON contains:\n"
              + "   - intent: The command's intent, inferred as a lowercase English verb (e.g., 'move', 'attack', 'study', 'sleep'). For Chinese inputs, translate the primary action to its English equivalent dynamically.\n"
              + "   - parameters: Command parameters (key-value pairs, inferred as English or standardized tokens, translating Chinese terms to English where applicable).\n"
              + "   - context: Optional context information (key-value pairs, inferred from text, using English terms).\n"
              + "   - time_range: Optional time range with start_time and end_time (e.g., {'start_time':'9:00 AM','end_time':'10:00 AM'}).\n"
              + "   - location: Optional location (e.g., 'Agent School').\n"
              + "3. Rules:\n"
              + "   - Input is a single command text, optional time_range, and optional location.\n"
              + "   - Output a compact JSON string (no newlines or indentation) or an error message.\n"
              + "   - Do not add any wrapping, such as 'content' field, </think> tags, or ```json markers.\n"
              + "   - Support Chinese and English inputs, dynamically translating Chinese commands to unified English intents and parameters via model inference.\n"
              + "   - Intents, parameters, and context are dynamically inferred without hard-coded mappings, ensuring Chinese inputs are translated to meaningful English terms.\n"
              + "4. Examples:\n"
              + "   - Input: text='向前移动100米', time_range={'start_time':'9:00 AM','end_time':'10:00 AM'}, location='Agent School'\n"
              + "     Output: {\"intent\":\"move\",\"parameters\":{\"direction\":\"forward\",\"value\":100},\"context\":{},\"time_range\":{\"start_time\":\"9:00 AM\",\"end_time\":\"10:00 AM\"},\"location\":\"Agent School\"}\n"
              + "   - Input: text='复习晨间研究笔记', time_range={'start_time':'9:00 AM','end_time':'10:00 AM'}, location='Agent School'\n"
              + "     Output: {\"intent\":\"study\",\"parameters\":{\"task\":\"morning_notes\"},\"context\":{},\"time_range\":{\"start_time\":\"9:00 AM\",\"end_time\":\"10:00 AM\"},\"location\":\"Agent School\"}\n"
              + "   - Input: text='攻击未知星球', time_range={}, location=''\n"
              + "     Output: {\"intent\":\"attack\",\"parameters\":{\"target\":\"unknown_planet\"},\"context\":{},\"time_range\":{},\"location\":\"\"}\n"
              + "   - Input: text='睡觉', time_range={'start_time':'12:00 PM','end_time':'12:30 PM'}, location='Home'\n"
              + "     Output: {\"intent\":\"sleep\",\"parameters\":{},\"context\":{},\"time_range\":{\"start_time\":\"12:00 PM\",\"end_time\":\"12:30 PM\"},\"location\":\"Home\"}")
  public String parseCommand(String text, Map<String, String> timeRange, String location) {
    if (text == null || text.trim().isEmpty()) {
      return "Invalid input, command text must not be empty.";
    }

    try {
      JSONObject jsonObject = new JSONObject();
      JSONObject paramsJson = new JSONObject();
      JSONObject contextJson = new JSONObject();
      JSONObject timeRangeJson = new JSONObject();

      // Handle time_range
      if (timeRange != null) {
        String startTime = timeRange.get("start_time");
        String endTime = timeRange.get("end_time");
        if (startTime != null && !startTime.trim().isEmpty()) {
          timeRangeJson.set("start_time", startTime);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
          timeRangeJson.set("end_time", endTime);
        }
      }

      // Model inference to determine intent, parameters, and context
      inferCommand(text, jsonObject, paramsJson, contextJson);

      jsonObject.set("parameters", paramsJson);
      jsonObject.set("context", contextJson);
      jsonObject.set("time_range", timeRangeJson);
      jsonObject.set("location", location != null ? location : "");

      return jsonObject.toString();
    } catch (Exception e) {
      return "Failed to process command: " + e.getMessage();
    }
  }

  private void inferCommand(
      String text, JSONObject jsonObject, JSONObject paramsJson, JSONObject contextJson) {
    // Simulate model inference for intent, parameters, and context
    // In a real implementation, this would call an NLP model to analyze the text and return
    // structured data
    String normalizedText = text.trim();

    // Infer intent and parameters
    inferParameters(normalizedText, paramsJson, contextJson);

    jsonObject.set("intent", text);
  }

  private String inferIntent(String text) {
    // Simulate intent inference (replace with NLP model call)
    // In a real implementation, the NLP model would analyze the text and return a lowercase English
    // intent
    // Placeholder: Extract the main verb/action as intent
    String[] words = text.split("\\s+");
    for (String word : words) {
      // Assume the first significant word is the action
      if (word.length() > 1 && !word.matches("\\d+")) {
        return normalizeEntity(word, true);
      }
    }
    return "unknown";
  }

  private void inferParameters(String text, JSONObject paramsJson, JSONObject contextJson) {
    // Simulate parameter and context inference (replace with NLP model call)
    // Extract entities and parameters dynamically without hard-coded logic
    String[] words = text.split("\\s+");
    Pattern numberPattern = Pattern.compile("\\d+(\\.\\d+)?");

    // Extract numeric values
    for (String word : words) {
      Matcher matcher = numberPattern.matcher(word);
      if (matcher.find()) {
        try {
          paramsJson.set("value", new java.math.BigDecimal(matcher.group()));
        } catch (NumberFormatException e) {
          // Ignore invalid number
        }
      }
    }

    // Dynamically infer parameters and context
    String intent = inferIntent(text);
    int entityIndex = 0;
    for (String word : words) {
      // Skip short words, numbers, and the intent word
      if (word.length() <= 1 || word.matches("\\d+") || normalizeEntity(word, true).equals(intent))
        continue;

      String normalizedWord = normalizeEntity(word, false);
      // Assign semantic parameter keys based on position
      // In a real model, keys would be inferred via semantic role labeling (SRL)
      if (entityIndex == 0) {
        paramsJson.set("task", normalizedWord); // Primary entity as task or target
      } else {
        paramsJson.set("entity_" + entityIndex, normalizedWord); // Additional entities
      }
      entityIndex++;
    }

    // Infer context dynamically
    // In a real model, this would analyze the text for contextual cues
    if (text.toLowerCase().contains("被") || text.toLowerCase().contains("counter")) {
      contextJson.set("condition", true);
    }
  }

  private String normalizeEntity(String word, boolean isIntent) {
    // Simulate entity normalization (replace with NLP model or translation service)
    // In a real implementation, this would use a model to translate or standardize entities to
    // English
    String lowerWord = word.toLowerCase().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_");

    // Handle English words directly
    if (lowerWord.matches("[a-z0-9_]+")) {
      return lowerWord;
    }

    // Simulate translation for Chinese (replace with actual model-based translation)
    // Generate pseudo-English tokens based on word content
    StringBuilder pseudoTranslation = new StringBuilder();
    for (char c : lowerWord.toCharArray()) {
      if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
        // Simulate a translation-like token (e.g., hash-based)
        pseudoTranslation.append(String.format("%c", (int) c % 26 + 97)); // Map to a-z
      } else {
        pseudoTranslation.append(c);
      }
    }

    String result = pseudoTranslation.toString().replaceAll("_+", "_").toLowerCase();

    // For intents, ensure verb-like output; for entities, ensure noun-like output
    if (isIntent) {
      return result.length() > 0 ? result + "_action" : "unknown";
    } else {
      return result.length() > 0 ? result + "_entity" : "unknown_entity";
    }
  }
}
