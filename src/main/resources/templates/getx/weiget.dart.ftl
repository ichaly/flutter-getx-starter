import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '${Utils.toUnderline(name!)}_logic.dart';
<#if defaultMode>
import '${Utils.toUnderline(name!)}_state.dart';
</#if>

<#if autoDispose>
class ${name!}${isPage?string(pageName,viewName)} extends StatefulWidget {
  @override
  _${name!}${isPage?string(pageName,viewName)}State createState() => _${name!}${isPage?string(pageName,viewName)}State();
}

class _${name!}${isPage?string(pageName,viewName)}State extends State<${name!}${isPage?string(pageName,viewName)}> {
  final ${name!}Logic logic = Get.put(${name!}Logic());
<#if defaultMode>
  final ${name!}State state = Get.find<${name!}Logic>().state;
</#if>

  @override
  void dispose() {
    Get.delete<${name!}Logic>();
    super.dispose();
  }
<#else>
class ${name!}${isPage?string(pageName,viewName)} extends StatelessWidget {
  final ${name!}Logic logic = Get.put(${name!}Logic());
<#if defaultMode>
  final ${name!}State state = Get.find<${name!}Logic>().state;
</#if>
</#if>

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
