package com.alienpoop.poopmcpserver.service;

import java.util.List;
import java.util.Map;

public interface I3DSceneCommandService {

  String parseCommand(String text, Map<String, String> timeRange, String location);
}
