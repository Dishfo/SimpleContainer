# SimpleContainer
一个简单且不完整的servlet容器 


tcp监听模块使用并发的i/o，接受数据传递给httpConntion
这个类面向字节流进行解析生成简单的httprequset 类，与对应的httpResponse 然后把控制权交给web容器

暂时简单实现了部分servletContext的功能 .还没有实现对容器进行初始化的模块

入口类为channel-componnet 模块下的 Main类. 运行前设置变量DISH_BASE_CONFIG
，值为配置文件路径

dish-config.xml 为实例配置文件

在项目目录下 mvn install -DskipTests 就可以打包一个可运行的jar

当前版本功能不完善 且有大量bug





