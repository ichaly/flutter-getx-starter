import 'package:get/get.dart';

import 'package:demo/app/modules/home/views/home_view.dart';
import 'package:demo/app/modules/home/bindings/home_binding.dart';

class Routes {
  static const HOME = '/home';

  static final routes = [
    GetPage(
      name: Routes.HOME,
      page: () => HomeView(),
      binding: HomeBinding(),
    ),
  ];
}