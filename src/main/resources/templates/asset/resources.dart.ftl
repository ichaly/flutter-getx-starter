/// Generated by Flutter GetX Starter on ${.now?string("yyyy-MM-dd HH:mm")}
import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

class R {
  /// This `R.image` struct is generated, and contains static references to static image asset resources.
  static const String package = "${packageName!}";
<#list res?keys as key>
  static const ${key!} = _R_${key?cap_first}();
</#list>
}

extension StringExtension on String {
  Widget toImage({
    Key key,
    double scale,
    double width,
    double height,
    Color color,
    BoxFit fit = BoxFit.contain,
    AlignmentGeometry alignment = Alignment.center,
    String package = R.package,
  }) {
    return Image.asset(
      this,
      key: key,
      scale: scale,
      width: width,
      height: height,
      color: color,
      fit: fit,
      alignment: alignment,
      package: package,
    );
  }

  Widget toLottie({
    Key key,
    double width,
    double height,
    String package = R.package,
    BoxFit fit = BoxFit.contain,
    Animation<double> controller,
    Function(LottieComposition) onLoaded,
    Alignment alignment = Alignment.center,
  }) {
    return Lottie.asset(
      this,
      key: key,
      width: width,
      height: height,
      fit: fit,
      alignment: alignment,
      controller: controller,
      onLoaded: onLoaded,
      package: package,
    );
  }
}
<#list res?keys as group>
class _R_${group?cap_first} {
  const _R_${group?cap_first}();

  <#list res[group]?keys as key>
  final String ${key!} = "${res[group][key]}";
  </#list>
}

</#list>
