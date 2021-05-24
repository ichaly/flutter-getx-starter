import 'package:get/get.dart';

<#if defaultMode??>
import '${Utils.toUnderline(name!)}_state.dart';
</#if>

class ${name!}Logic extends GetxController {
<#if defaultMode??>
  final state = ${name!}State();
</#if>

  @override
  void onInit() {
    // TODO: implement onInit
    super.onInit();
  }

  @override
  void onReady() {
    // TODO: implement onReady
    super.onReady();
  }

  @override
  void onClose() {
    // TODO: implement onClose
    super.onClose();
  }
}
