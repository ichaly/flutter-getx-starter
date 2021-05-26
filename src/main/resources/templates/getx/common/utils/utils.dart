import 'dart:convert';
import 'dart:io';

import 'package:convert/convert.dart';
import 'package:crypto/crypto.dart';

class Utils {
  ///格式化播放时间
  static formatSeconds(double millisecond) {
    int _second = millisecond ~/ 1000;
    if (_second <= 0) {
      return "00:00";
    } else if (_second < 60) {
      String second = (_second % 60).toInt().toString().padLeft(2, '0');
      return "00:$second";
    } else if (_second < 3600) {
      String second = (_second % 60).toString().padLeft(2, '0');
      String minute = (_second ~/ 60).toString().padLeft(2, '0');
      return "$minute:$second";
    } else {
      String second = (_second % 60).toString().padLeft(2, '0');
      String minute = (_second % 3600 ~/ 60).toString().padLeft(2, '0');
      String hour = (_second ~/ 3600).toString().padLeft(2, '0');
      return "$hour:$minute:$second";
    }
  }

  ///格式化大于等于1万的数字为x.x万，如果小于等于0时则显示占位符
  static String formatThousand(num, {placeholder = "赞"}) {
    if (num <= 0) {
      return placeholder;
    } else if (num < 10000) {
      return num.toString();
    } else {
      return "${(num / 10000.0).toStringAsFixed(1)}万";
    }
  }

  /// md5 加密
  static String getMD5(String data) {
    var content = Utf8Encoder().convert(data);
    var digest = md5.convert(content);
    return hex.encode(digest.bytes);
  }

  /// 解析URL参数
  static Map parseQuery(String? url) {
    var parame = {};
    if (url != null && url.isNotEmpty && url.contains("?")) {
      url.split("?")[1].split("&").forEach((str) {
        var pair = str.split("=");
        String _key = pair[0],
            _value = pair[1];
        if (_key.isNotEmpty && _value.isNotEmpty) {
          parame[_key] = _value;
        }
      });
    }
    return parame;
  }

  static String parsePageName(String? url) {
    String _pageName = "";
    if (url != null && url.isNotEmpty) {
      if (url.contains("?")) {
        _pageName = url.split("?")[0];
      } else {
        _pageName = url;
      }
    }
    return _pageName;
  }
}
