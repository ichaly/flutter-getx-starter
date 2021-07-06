/// Generated by Flutter GetX Starter on ${.now?string("yyyy-MM-dd HH:mm")}

import 'package:flutter/material.dart';
import 'package:get/get_navigation/get_navigation.dart';
import './app/common/app_route.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      initialRoute: Routes.HOME,
      getPages: Routes.routes,
    );
  }
}