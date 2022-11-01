# 스프링 부트에서 MVC

* 강좌에서는 두가지 의존성만 넣었다
  * spring-boot-starter-thymeleaf
  * spirng-boot-starter-web

## DispatcherServlet
### handlerMapping
* SimpleUrlHandlerMapping
  * 빈 이름 : 파비콘 용 핸들러 매핑 
* RequestMappingHandlerMapping
  * 어노테이션 기반 핸들러를 찾아 주는 것
* BeanNameUrlHandlerMapping
  * 잘 쓰지는 않는다
  * /hello* 라면 /hello1, /helloworld 등 만족하는 핸들러를 찾아준다
* SimpleUrlHandlerMapping
  * 빈 이름 : resourceHandlerMapping
  * /src/main/resources/static의 정적 자원을 연결하는 역할을 해줌
* WelcomePageHandlerMapping
  * 리소스 핸들러 매핑 역할 - 인덱스 페이지 지원 역할

### handlerAdapter
* RequestMappingHandlerAdapter
  * 어노테이션 기반 핸들러들을 처리 하는 역할
* HttpRequestHandlerAdapter
* SimpleControllerHandlerAdapter

### viewResolvers (강좌에서 Thymeleaf의 의존성을 추가 했을 때)
* ContentNegotiationViewResolver
  * 밑에 있는 4개의 Resolver에게 위임하는 역할 
  * 요청이 어떤 뷰를 원하는가에 맞게 찾아 주는 역할
  * 우선순위가 제일 높음
* BeanNameViewResolver
* ThymeleafViewResolver
* ViewResolverComposite
* InternalResourceViewResolver

#### 이런 설정들은 어디서 왔는가?
* spring-boot-autoconfigure의 spring.factories의 EnableAutoConfiguration의 자동 설정 파일 에서 적용
* spring-boot type은 3개다
  * SERVLET
  * WEBFLUX
  * NONE-WEB

## 스프링 MVC 커스터마이징
* application.properties
  * WebMvcAutoConfiguration의 properties를 가져 와서 설정 등록 해줌
* @Configuration + Implements WebMvcConfigurer
  * 스프링 부트의 스프링 MVC 자동설정 + 추가 설정
* @Configuration + @EnableWebMvc + implements WebMvcConfigurer
  * 스프링 부트의 스프링 MVC 자동 설정 사용X