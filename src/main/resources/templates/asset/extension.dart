import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

const String package = "";

extension StringExtension on String {
  Widget toImage({
    Key key,
    double scale,
    double width,
    double height,
    Color color,
    BoxFit fit = BoxFit.contain,
    String package = package,
    AlignmentGeometry alignment = Alignment.center,
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
    BoxFit fit = BoxFit.contain,
    Alignment alignment = Alignment.center,
    String package = package,
  }) {
    return Lottie.asset(
      this,
      key: key,
      width: width,
      height: height,
      fit: fit,
      alignment: alignment,
      package: package,
    );
  }
}