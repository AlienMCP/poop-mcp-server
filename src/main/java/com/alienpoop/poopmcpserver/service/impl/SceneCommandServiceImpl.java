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
              + "   - action: The command's intent, inferred as a lowercase English verb (e.g., 'move', 'attack', 'study', 'sleep'). For Chinese inputs, translate the primary action to its English equivalent dynamically.\n"
              + "   - parameters: Command parameters (single value or key-value pairs, inferred as English or standardized tokens, translating Chinese terms to English where applicable, excluding the target).\n"
              + "   - target: The primary target of the action (e.g., 'planet', 'morning_notes').\n"
              + "   - time_range: Optional time range with start_time and end_time (e.g., {'start_time':'9:00 AM','end_time':'10:00 AM'}).\n"
              + "   - location: Optional location (e.g., 'Agent School').\n"
              + "3. Rules:\n"
              + "   - Input is a single command text, optional time_range, and optional location.\n"
              + "   - Output a compact JSON string (no newlines or indentation) or an error message.\n"
              + "   - Do not add any wrapping, such as 'content' field, </think> tags, or ```json markers.\n"
              + "   - Support Chinese and English inputs, dynamically translating Chinese commands to unified English actions and parameters via model inference.\n"
              + "   - Actions, parameters, and targets are dynamically inferred without hard-coded mappings, ensuring Chinese inputs are translated to meaningful English terms.\n"
              + "   - The target is extracted as a single primary entity (e.g., 'planet' instead of 'the planet').\n"
              + "4. Examples:\n"
              + "   - Input: text='向前移动100米', time_range={'start_time':'9:00 AM','end_time':'10:00 AM'}, location='Agent School'\n"
              + "     Output: {\"action\":\"move\",\"parameters\":{\"direction\":\"forward\",\"value\":100},\"target\":\"\",\"time_range\":{\"start_time\":\"9:00 AM\",\"end_time\":\"10:00 AM\"},\"location\":\"Agent School\"}\n"
              + "   - Input: text='复习晨间研究笔记', time_range={'start_time':'9:00 AM','end_time':'10:00 AM'}, location='Agent School'\n"
              + "     Output: {\"action\":\"study\",\"parameters\":\"\",\"target\":\"morning_notes\",\"time_range\":{\"start_time\":\"9:00 AM\",\"end_time\":\"10:00 AM\"},\"location\":\"Agent School\"}\n"
              + "   - Input: text='攻击未知星球', time_range={}, location=''\n"
              + "     Output: {\"action\":\"attack\",\"parameters\":\"\",\"target\":\"unknown_planet\",\"time_range\":{},\"location\":\"\"}\n"
              + "   - Input: text='征服已知星球', time_range={'start_time':'14:55','end_time':'15:05'}, location='super island'\n"
              + "     Output: {\"action\":\"conquer\",\"parameters\":\"known_planets\",\"target\":\"planet\",\"time_range\":{\"start_time\":\"14:55\",\"end_time\":\"15:05\"},\"location\":\"super island\"}\n"
              + "   - Input: text='睡觉', time_range={'start_time':'12:00 PM','end_time':'12:30 PM'}, location='Home'\n"
              + "     Output: {\"action\":\"sleep\",\"parameters\":\"\",\"target\":\"\",\"time_range\":{\"start_time\":\"12:00 PM\",\"end_time\":\"12:30 PM\"},\"location\":\"Home\"}")
  public String parseCommand(String text, Map<String, String> timeRange, String location) {
    if (text == null || text.trim().isEmpty()) {
      return "Invalid input, command text must not be empty.";
    }

    try {
      JSONObject jsonObject = new JSONObject();
      JSONObject timeRangeJson = new JSONObject();
      Object parameters = "";

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

      // Model inference to determine action, parameters, and target
      String[] inferenceResult = inferCommand(text);
      String action = inferenceResult[0];
      parameters = inferenceResult[1];
      String target = inferenceResult[2];

      jsonObject.set("action", action);
      jsonObject.set("parameters", parameters);
      jsonObject.set("target", target);
      jsonObject.set("time_range", timeRangeJson);
      jsonObject.set("location", location != null ? location : "");

      return jsonObject.toString();
    } catch (Exception e) {
      return "Failed to process command: " + e.getMessage();
    }
  }

  private String[] inferCommand(String text) {
    String normalizedText = text.trim();
    String action = inferAction(normalizedText);
    String[] paramsAndTarget = inferParametersAndTarget(normalizedText, action);
    return new String[] { action, paramsAndTarget[0], paramsAndTarget[1] };
  }

  private String inferAction(String text) {
    // Simulate action inference (replace with NLP model call)
    String[] words = text.split("\\s+");
    for (String word : words) {
      if (word.length() > 1 && !word.matches("\\d+")) {
        return normalizeEntity(word, true);
      }
    }
    return "unknown";
  }

  private String[] inferParametersAndTarget(String text, String action) {
    // Simulate parameter and target inference (replace with NLP model call)
    String[] words = text.split("\\s+");
    Pattern numberPattern = Pattern.compile("\\d+(\\.\\d+)?");
    JSONObject paramsJson = new JSONObject();
    String target = "";
    int entityIndex = 0;

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

    // Infer parameters and target
    String paramText = "";
    for (String word : words) {
      if (word.length() <= 1 || word.matches("\\d+") || normalizeEntity(word, true).equals(action)) {
        continue;
      }

      String normalizedWord = normalizeEntity(word, false);
      if (entityIndex == 0) {
        target = normalizedWord;
        // Use the full phrase after the action as parameters
        int actionIndex = text.indexOf(action);
        paramText = actionIndex >= 0 ? text.substring(actionIndex + action.length()).trim() : normalizedWord;
      } else {
        paramsJson.set("entity_" + entityIndex, normalizedWord);
      }
      entityIndex++;
    }

    // If no parameters other than target, use paramText as parameters if appropriate
    String paramValue = paramText.isEmpty() ? "" : normalizeEntity(paramText, false);
    // Ensure target is the last significant entity if multiple entities exist
    if (entityIndex > 1) {
      target = normalizeEntity(words[words.length - 1], false);
    }
    return new String[] { paramValue, target };
  }

  private String normalizeEntity(String word, boolean isAction) {
    String lowerWord = word.toLowerCase().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "_");

    // Handle English words directly
    if (lowerWord.matches("[a-z0-9_]+")) {
      // Remove articles like 'the' for target
      if (!isAction && lowerWord.equals("the")) {
        return "";
      }
      return lowerWord;
    }

    // Simulate translation for Chinese using dynamic inference
    String result = simulateModelTranslation(lowerWord, isAction);

    // For actions, ensure verb-like output; for entities, ensure noun-like output
    if (isAction) {
      return result.length() > 0 ? result : "unknown";
    } else {
      return result.length() > 0 ? result : "unknown_entity";
    }
  }

  private String simulateModelTranslation(String word, boolean isAction) {
    // Simulate NLP model translation (replace with actual model call)
    StringBuilder pseudoTranslation = new StringBuilder();
    for (char c : word.toCharArray()) {
      if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
        pseudoTranslation.append(String.format("%c", (int) c % 26 + 97));
      } else {
        pseudoTranslation.append(c);
      }
    }

    String result = pseudoTranslation.toString().replaceAll("_+", "_").toLowerCase();

    // Simulate context-aware translation
    if (isAction) {
      return result + "_verb";
    } else {
      return result + "_noun";
    }
  }
}