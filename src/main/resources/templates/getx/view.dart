import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'logic.dart';
import 'state.dart';

class $namePage extends StatelessWidget {
  final $nameLogic logic = Get.put($nameLogic());
  final $nameState state = Get.find<$nameLogic>().state;

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
