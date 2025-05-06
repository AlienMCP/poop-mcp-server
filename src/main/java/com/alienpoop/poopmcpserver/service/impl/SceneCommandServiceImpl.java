package com.alienpoop.poopmcpserver.service.impl;

import cn.hutool.json.JSONObject;
import com.alienpoop.poopmcpserver.annotation.ToolServer;
import com.alienpoop.poopmcpserver.service.I3DSceneCommandService;
import java.math.BigDecimal;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@ToolServer
@Service
public class SceneCommandServiceImpl implements I3DSceneCommandService {

  @Override
  @Tool(
      name = "processMoveCommand",
      description =
          "1. Parse the move command parameters from user input (e.g., \"move forward 500 meters\"):\n"
              + "   - command: Fixed to \\\"move\\\" (recognize \\\"move\\\" or \\\"walk\\\").\\n\"\n"
              + "              + \"   - target: Map to \\\"front\\\" (forward), \\\"back\\\" (backward), \\\"left\\\" (left), \\\"right\\\" (right).\\n\"\n"
              + "              + \"   - distance: Extract the number (integer or decimal, e.g., 500).\\n\"\n"
              + "              + \"2. Call processMoveCommand(command, target, distance).\\n\"\n"
              + "              + \"3. Directly return the raw output of processMoveCommand (the compact JSON string from jsonObject.toString() or an error message), e.g., {\\\"command\\\":\\\"move\\\",\\\"target\\\":\\\"front\\\",\\\"distance\\\":500} or \\\"Invalid distance, must be a positive number.\\\".\\n\"\n"
              + "              + \"4. Must adhere to the following rules:\\n\"\n"
              + "              + \"   - Do not add any wrapping, such as \\\"content\\\" field, </think> tags, ```json markers, or other metadata.\\n\"\n"
              + "              + \"   - Do not format the JSON string; keep it compact (no newlines or indentation).\\n\"\n"
              + "              + \"   - Do not translate, rephrase, or modify the output in any way.\n"
              + "   - The output must be identical to the string returned by the function, containing only the JSON string or error message.")
  public String processMoveCommand(String command, String target, BigDecimal distance) {
    // Validate input parameters
    if (command == null || !command.equals("move")) {
      return "Invalid command type, only 'move' is supported.";
    }
    if (target == null || !isValidTarget(target)) {
      return "Invalid direction, only 'front', 'back', 'left', 'right' are supported.";
    }
    if (distance == null || distance.compareTo(BigDecimal.ZERO) <= 0) {
      return "Invalid distance, must be a positive number.";
    }

    JSONObject jsonObject = new JSONObject();
    jsonObject.set("command", command);
    jsonObject.set("target", target);
    jsonObject.set("distance", distance);

    return jsonObject.toString();
  }

  private boolean isValidTarget(String target) {
    return "front".equals(target)
        || "back".equals(target)
        || "left".equals(target)
        || "right".equals(target);
  }
}
