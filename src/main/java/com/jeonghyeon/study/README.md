# MVC

## Servlet 관련

### 순서
1. ServletContextListener 
2. Filter
3. Servlet  

## spring-web 관련

### 주요 키워드(설명 추가 예정)
* RootApplicationContext
  * Web에 관련된 것은 없다 (Service, Repositorys)
* WebApplicationContext
  * Servlet WebApplicationContext
    * RootApplicationContext를 상속 받아 만든 것이다 
    * DispatcherServlet에 의해 만들어 준다
    * Controller, ViewResolver, HandlerMapping이 있다.
* ContextLoaderListener


### Spring VS SpringBoot
* 스프링
  * 톰캣안에 스프링을 넣은 형태 (서블릿 컨텍스트 안에 스프링이 들어간 구조)
* 스프링 부트
  * 스프링 부트 어플리케이션 안에 톰캣을 넣은 구조 (임베디드 톰캣)