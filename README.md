# geneNomalClasses
It is aimed to generate some normal classes,like xxDTO,xxxVO,xxMapper ,etc,
基于一张mysql表,生成对应的rest接口,servcice,domain等;
避免无意义的重复劳动

ps:
1 默认使用了lombok，
2 生成的类里面，日期类型使用的jdk8的localDateTime；
  rest中，请求返回页面，可能localDateTime类型格式化有异常，需额外增加 jackson-datatype-jsr的包，适配jdk的localDateTime类型
3 mapper是注解式sql
