# 课堂点名器

示意图如下：![](http://static2.mazhangjing.com/20200513/82d9a1b_GIF2020-5-1315-29-30.gif)

之前上大学的时候有一个姓黄的老师总是提问学生，因此他在课堂上用了一个叫做点名器的小软件，据说是他自己用 VB 写的，点一下出来一个人的名字，然后抽他回答问题。

某天中午闲来无事，睡不着觉，就寻思实现一下这个小工具，先是搭了模型，提供不重复抽样和简单放回抽样，以及抽样重置方法，然后添加了按行读取文本文档的数据输入方法，最后提供了一个 JavaFx 写的 GUI，用来响应鼠标拖拽和点击、划过的事件。

程序是 Scala 写的，基于 trait 的 MVC 分离 + 模型测试，代码结构还不错。使用了 ScalaFx、ScalaTest、Logback 等库。

![](http://static2.mazhangjing.com/badge/openjdk.png)
![](http://static2.mazhangjing.com/badge/javafx.png)
![](http://static2.mazhangjing.com/badge/scala.png)
