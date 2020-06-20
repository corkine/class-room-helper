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

我的心得有两点：

1. 将样式和结构分离 CSS 和 UI 组件分离

2. 将视图和动作分离。从模型到界面的关系非常复杂，一般而言，我们希望界面是静态的，但模型却是动态的，因此这就需要 Controller 从中调节。我们并不希望在 Controller 中，一堆的界面组件和一堆的模型变量互相随意调用，而是希望其彼此都是高内聚，从整体看是低耦合的。这一点可以通过 Property 的绑定和监听器来做到，让模型和界面都依赖于 Property，这样就创造出了中间层，对于模型而言，Property 是动态的，可以随便更改其值，而对界面而言，它看起来是静态的，因为从语法上而言，它仅仅是动态绑定，并没有带有副作用的操作。当然，可以有很多 Property，其中间可以通过监听器或者 XXXBinding 定义关系。

![](http://static2.mazhangjing.com/20200620/8d8132b_cm_image2020-06-2010.45.52.png)

有些时候，需要在一个动作中操纵多个 Property，且创建新的 Property 并且建立它和这些被操纵的 Property 之间的关系的做法过于繁琐/低效/不直观（典型的，比如非常核心的操作、IO 等耗时的操作），可以将其聚合成为一个 Action（Controller Method），以提升程序语义清晰性，这样就构建了一个从动态模型到静态界面之间良好的通路。