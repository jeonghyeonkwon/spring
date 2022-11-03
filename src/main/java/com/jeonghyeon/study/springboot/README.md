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

## 스프링부트에서 war 패키징
* 톱캣이나 서블릿 앤진을 사용할 때는 war파일로 패키징 해야 됨
```java
public class ServletInitializer extends SpringBootServletInitializer{
    @Override
    protected SpringApplicationBuilder configurer(SpringApplicationBuilder application){
        return application.sources(스프링부트_애플리케이션.class);
    }
}
```

## 스프링부트에서 JSP 
* 제약사항
  * JAR 프로젝트로 만들 수 없음, WAR 프로젝트로 만들어야 됨
  * java -jar 실행할 수는 있지만 "실행가능한 jar 파일"을 지원하지 
    * jar를 응용 프로그램? 같은거를 만들 수 없다는 뜻인가?
  * 언더토우는 jsp를 지원하지 않음
    * undertow
      * tomcat, jetty, netty와 같은 was
  * Whitelabel 에러 페이지를 error.jsp로 오버라이딩 할 수 없음

## 스프링 부트 테스트

```java

import org.springframework.beans.factory.annotation.Autowired;

@RunWith(SpringRunner.class)
@WebMvcTest
public class SampleControllerTest {
  @Autowired
  MockMvc mockMvc;
  
  @Test
  public void hello() throws Exception{
      this.mockMvc.perform(get("/hello"))
              .andDo(print())
              .param("name","jeonghyeon")
              .andExpect(content().string("hello"));
  }
}
```

* @WebMvcTest
  * 웹과 관련된 빈 들만 등록 해준다.
  * [spring5 - README.md의 formatter]()를 @Component로 빈 등록 했더라도 웹 관련 빈이라고 인식 못함
* @SpringBootTest
  * 통합 테스트용 빈
  * 모든 빈들을 등록 (@SpringBootApplicaiton 부터)
  * 이러면 MockMvc는 등록되지 않아 @AutoConfigureMockMvc도 추가 해야 됨
