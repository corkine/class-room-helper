# 课堂点名器

示意图如下：![](http://static2.mazhangjing.com/20200513/82d9a1b_GIF2020-5-1315-29-30.gif)

之前上大学的时候有一个姓黄的老师总是提问学生，因此他在课堂上用了一个叫做点名器的小软件，据说是他自己用 VB 写的，点一下出来一个人的名字，然后抽他回答问题。

某天中午闲来无事，睡不着觉，就寻思实现一下这个小工具，先是搭了模型，提供不重复抽样和简单放回抽样，以及抽样重置方法，然后添加了按行读取文本文档的数据输入方法，最后提供了一个 JavaFx 写的 GUI，用来响应鼠标拖拽和点击、划过的事件。

程序是 Scala 写的，基于 trait 的 MVC 分离 + 模型测试，代码结构还不错。使用了 ScalaFx、ScalaTest、Logback 等库。

![](http://static2.mazhangjing.com/badge/openjdk.png)
![](http://static2.mazhangjing.com/badge/javafx.png)
![](http://static2.mazhangjing.com/badge/scala.png)

## 注意

这个小工具是我对于 JavaFx Property 和 Binding 风格 MVC 开发的一些思考，吸纳了 Scala mixin 风格代码特点：

写出这样的代码很难，反直觉，但遵循如下步骤可以做到：

1. 将样式和结构分离 CSS 和 UI 组件分离

2. 将视图和动作分离，分离时优先使用 Property 而非 Controller Method，引起其能够尽可能地降低 Controller 和 View 的各自对对方的依赖关系（Controller 中使用过多 View 组件引用，导致组件全部必须暴露和作为类变量，而不能像 Flutter/ScalaFx 那样按照结构良好的嵌套，对于大工程而言，铺平组件没有嵌套组件容易读；View 中调用过多 Controller 方法，因为 View 调用基本嵌套在组件中，因此很难寻找，如果 Controller 方法修改，除了 IDE 的自动重构，寻找其在 View 的调用非常困难，而 Property 则远少于 UI 组件，因此其放在类变量中，平整易读，对于 View 和 Controller 的修改都更清晰）。

ps. 这种分离很常见，react.js 和 flutter 使用的是对状态进行控制，只允许某种方法来更新状态，其不灵活，而 JavaFx 的 Property 本身是值而不是变量，Binding 本质是 Listener，因此能力较灵活，虽然丧失了一些数据变化流动性观察的入口，但整体而言，代码结构很清晰，值得选择。
