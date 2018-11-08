# SimpleContainer
一个简单且不完整的servlet容器 


tcp监听模块使用并发的i/o，接受数据传递给httpConntion
这个类面向字节流进行解析生成简单的httprequset 类，与对应的httpResponse 然后把控制权交给web容器


入口类为channel-componnet 模块下的 Main类. 运行前设置变量DISH_BASE_CONFIG
，值为配置文件路径

dish-config.xml 为实例配置文件




