import 'dart:convert';
import 'dart:developer' as developer;

import 'package:flutter/foundation.dart';

class Logger {
  static String tag = "ZhiQ";
  static bool isLogEnable = true;
  static bool isDebugEnable = true;

  static void _printWrapped(String text, {wrapWidth}) {
    try {
      if (wrapWidth != null) {
        final pattern = new RegExp('.{1,$wrapWidth'); // wrapWidth is the size of each chunk
        pattern.allMatches(text).forEach((match) => developer.log(match.group(0), name: tag));
      } else {
        developer.log(text, name: tag);
      }
    } catch (e) {}
  }

  static debug(Object message, {int wrapWidth, formatJson = true}) {
    if (!kReleaseMode && isDebugEnable) {
      log(message, wrapWidth: wrapWidth, formatJson: formatJson);
    }
  }

  static log(Object message, {int wrapWidth, formatJson = false}) {
    if (isLogEnable) {
      try {
        if (formatJson) {
          message = JsonEncoder.withIndent('  ').convert(json.decode(message));
        }
      } catch (e) {}
      _printWrapped(message);
    }
  }
}
