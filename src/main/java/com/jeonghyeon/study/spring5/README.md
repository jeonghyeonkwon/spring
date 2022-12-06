# MVC

## Servlet 관련

### 순서
1. ServletContextListener 
2. Filter
3. Servlet  

## spring-web 관련

### 주요 키워드(설명 추가 예정)
* RootApplicationContext
  * Web에 관련된 것은 없다 (Service, Repository)
* WebApplicationContext
  * Servlet WebApplicationContext
    * RootApplicationContext를 상속 받아 만든 것이다 
    * DispatcherServlet에 의해 만들어 준다
    * Controller, ViewResolver, HandlerMapping이 있다.
* ContextLoaderListener

### DispatcherServlet
#### 순서
1. 요청을 분석한다 (multipart, local 등)
2. 핸들러들을 찾아온다(HandlerMapping) - 디자인 패턴 중 Strategy 패턴을 사용하였다 
   * BeanNameUrlHandlerMapping
     * DispatcherServlet이 기본으로 제공하는 것
   * RequestMappingHandlerMapping
     * 에노테이션 같은 것으로 설정한 (GET,POST,...) 찾아주는 것
3. HandlerAdapter를 가져 온다  - 찾아온 핸들러를 실행하는 인터페이스
   * HttpRequestHandlerAdapter
   * SimpleControllerHandlerAdapter
   * RequestMappingHandlerAdapter
4. 그 이후 컨트롤러의 메소드를 리플랙션 하여 내용들을 실행 (handlerMethod안에 메소드에 대한 정보가 있다)
   * @GetMapping("/index") 같은 것들
5. 예외가 발생한다면, 예외 처리 핸들러에 요청 처리를 위임
6. 핸들러의 리턴값 처리를 판단
   * 뷰 이름에 해당하는 뷰를 찾아서 모델 데이터를 랜더링한다
   * @ResponseBody가 있다면 Converter를 사용해서 응답 본문을 만들고 응답을 보낸다.
     * 의존성으로 추가해 줘야 RestApi가 작동되었음 (실습 해봤을 때)
     * implementation group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.14.0-rc3'

#### DispatcherServlet 구성 요소(각 인터페이스)
* MultipartResolver
  * 파일 업로드 관련 인터페이스 
* LocalResolver
  * 위치 정보를 파악하는 인터페이스
  * accept-language
* ThemeResolver
  * 애플리케이션에 설정된 테마를 파악하고 변경할 수 있는 인터페이스
* HandlerMapping
  * 요청을 처리할 핸들러를 찾는 인터페이스
* HandlerAdapter
  * HandlerMapping에서 찾은 핸들러를 처리하는 인터페이스
* HandlerExceptionResolvers
  * 요청 처리 중에 발생한 에러 처리하는 인터페이스
* RequestToViewNameTranslator
  * 핸들러에서 뷰 이름을 명시적으로 리턴하지 않는 경우, 요청을 기반으로 뷰 이름을 판단하는 인터페이스
  * return 값이 void라도 @GetMapping("/sample")이면 return을 sample로 뷰를 찾아줌 
* ViewResolvers
  * 뷰 이름에 해당하는 뷰를 찾아내는 인터페이스
* FlashMapManger
  * FlashMap 인스턴스를 가져오고 저장하는 인터페이스
  * redirect를 할 때 전송을 더 편하게 하기 위한 것

## web.xml 없이 spring-mvc 하는 방법
* 밑의 xml 코드와 동일한 코드
```java

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebApplication
        implements WebApplicationInitializer
{
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(servletContext);
        context.register(WebConfig.class);
        context.refresh();

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic app = servletContext.addServlet("app", dispatcherServlet);
        app.addMapping("/app/*");
    }
}
```

```xml
<!DOCTYPE web-app PUBLIC
        "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
        "http://java.sun.com/dtd/web-app_2_3.dtd">
<web-app>
    <display-name>Archetype Created Web Application</display-name>
    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
        </init-param>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.jeonghyeon.study.spring5.WebConfigcom.jeonghyeon.study.spring5.WebConfig</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>

</web-app>
```

### Spring VS SpringBoot
* 스프링 
  * 톰캣안에 스프링을 넣은 형태 (서블릿 컨텍스트 안에 스프링이 들어간 구조)
* 스프링 부트
  * 스프링 부트 어플리케이션 안에 톰캣을 넣은 구조 (임베디드 톰캣)

## @EnableWebMvc 와 WebMvcConfigurer
* 애노테이션 기반 스프링 MVC를 사용할 때 편리한 웹 MVC 기본 설정
  * 인터셉터나 메시지 컨버터 같은 것을 추가하기 쉬워진다.
* 확장할려면 WebMvcConfigurer인터페이스를 이용하여 확장하면 된다.
  * @EnableWebMvc가 제공하는 빈을 WebMvcConfigurer로 커스터마이징 할 수 있다.

```java
@Configuration
@ComponentScan
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/",".jsp");
    }
}
```

## 직접 타임리프 구현하면서 
* WebConfig 설정파일
```java

@Configuration
@ComponentScan
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{
    @Autowired
    private ApplicationContext applicationContext;
    @Bean
    public SpringResourceTemplateResolver templateResolver(){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(true);
        return templateResolver;

    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setTemplateResolver(templateResolver());
        springTemplateEngine.setEnableSpringELCompiler(true);
        return springTemplateEngine;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setContentType("text/html");
        viewResolver.setCharacterEncoding("UTF-8");
        return viewResolver;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafViewResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    }


}
```
* Gradle
```
dependencies {
    compileOnly 'javax.servlet:javax.servlet-api:4.0.1'
    testImplementation 'junit:junit:4.11'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.2'
    implementation 'org.springframework:spring-webmvc:5.3.23'
    testImplementation group: 'org.springframework', name: 'spring-test', version: '5.3.23'
    implementation group: 'org.thymeleaf', name: 'thymeleaf', version: '3.1.0.RC1'
    implementation group: 'org.thymeleaf', name: 'thymeleaf-spring5', version: '3.1.0.RC1'
    implementation group: 'ch.qos.logback', name: 'logback-classic', version: '1.4.4'
    implementation group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.14.0-rc3'
    compileOnly group: 'org.projectlombok', name: 'lombok', version: '1.18.24'
    annotationProcessor( group: 'org.projectlombok', name: 'lombok', version: '1.18.24')
}

```


## Formatter

```java
@GetMapping("/hello/{name}")
public String hello(@PathVariable("name") Person person){
    // name이라는 path를 Person이라는 객체에 넣어 주고 싶을 때 사용    
}

```

### Formatter 만들기

```java
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class PersonFormatter implements Formatter<Person> {
    // 어떤 문자열을 객체로 변환할 것인가?
    @Override
    public Person parse(String text, Locale locale) throws ParseException {
        Person person = new Person();
        person.setName(text);
        
        return person;
    }
    // 해당 객체를 문자열로 어떻게 출력할 것인가
    @Override
    public String print(Person object, Locale locale) {
        return object.toString();
    }
}
```
### 만든 Formatter config에 추가

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new PersonFormatter());
        
    }
}
```

* 스프링에서만 이렇게 쓰지 부트에서는 @Component으로 빈 등록 하면 된다.


## 핸들러 인터셉터
* 핸들러 매핑에 설정할 수 있는 인터셉터
* 여러 핸들러에서 반복적으로 사용하는 코드를 중이고 싶을 때 사용
  * 로깅, 인증 체크, Locale 변경

### 순서
1. preHandle (1)
   * boolean preHandle(request,response,handler)
2. preHandle (2)
3. 요청 처리
4. postHandle (2)
   * void postHandle(request,response,modelAndView)
5. postHandle (1)
6. 뷰 랜더링
7. afterCompletion (2)
8. afterCompletion (1)

### 서블릿 필터와 차이점
* 서블릿 필터 보다 구체적인 처리가 가능하다
  * handler나 modelAndView를 지원하기 때문에
* 서블릿 필터은 일반적인 용도의 기능을 구현하는데 좋다
  * 스프링의 특화된 기술이 아닌 것들 (Xss 공격 처리하는 것들 - Luccy)

### 구현
```java
@Configuration
@ComponentScan
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GreetingInterceptor())
                .addPathPatterns("/hi")
                .order(0);
    }
}

public class GreetingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion");
    }
}

```

## 리소스 핸들러
* 정적인 리소스를 처리하는 핸들러
* 톰캣, jetty, 언더토우 같은것에는 디폴트 서블릿들이 각각 있다
  * 스프링은 이 디폴트 서블릿에 요청을 위임해서 정적인 리소스를 처리한다.
    * 이렇게 하면 정적인 리소스가 먼저 처리되어 만든 핸들러가 작동 안하기 때문에 정적인 리소스 핸들러를 우선순위를 낮게 한다.
* 스프링 부트에서는 기본적으로 정적 리소스 핸들러와 캐싱 제공한다.
  * static 디렉토리 밑에 index.html 파일을 만들고 body에 hello index 작성 후 밑에 코드를 테스트 하면 참이 뜬다
```java
@Test
public void helloStatic() throws Exception{
    this.mockMvc.perform(get("/index.html"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(Matchers.containsString("hello index")));
}
```
### 리소스 핸들러 설정하는 법
* classpath 이외에도 파일 시스템 기반도 가능하다
  * classpath를 주지 않으면 src/main/web/app로 찾는다
```java

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mobile/**") // url 요청이 들어오면
                .addResourceLocations("classpath:/mobile/") //여기서 찾아라 classpath:/ 는 java와 resources 디렉토리 둘 다 이다.
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)); // 캐시 설정도 가능
        
    }
}
/*
 * network의 Headers에 If-Modified-Since 로 체크를 한다 
 * 
 * */
// 테스트에서 캐시 테스트도 가능
@Test
public void helloStatic() throws Exception {
    this.mockMvc.perform(get("/mobile/index.html"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(Matchers.containsString("hello mobile")))
            .andExpect(heder().exists(HttpHeaders.CACHE_CONTROL));
}

```

## 메시지 컨버터
* 스프링에서 메시지 컨버터 사용
  * request = @RequestBody
  * response = @ResponseBody + @Controller = @RestController
* request의 contentType에 따라 밑의 컨버터 등이 결정됨
### 기본 HTTP 메시지 컨버터( 괄호에 있는 것들은 라이브러리를 추가 해야됨)
* 바이트 배열 컨버터
* 문자열 컨버터
* Resource 컨버터
* Form (폼 데이터 to/from MultiValueMap<String, String>)
* (JAXB2 컨버터)
  * XML용
* (Jackson2 컨버터)
  * Json용
* (Jackson 컨버터)
  * Json용
* (Gson 컨버터)
  * Json용
* (Atom 컨버터)
  * Atom 피드
* (RSS 컨버터)
  * Rss 피드
### 메시지 컨버터 등록 방법
* webconfig에서 추가
```java
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcConfigurer.super.configureMessageConverters(converters);
} // 기존 컨버터들을 사용 못함

@Override
public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcConfigurer.super.extendMessageConverters(converters);
} // 기존 컨버터에 추가
```
* 의존성 라이브러리로 추가
  * 메이븐 또는 그레이들에 의존성을 추가하면 컨버터가 자동으로 등록됨
  * WebMvcConfigurationSupport
    * 스프링 MVC 기능임
    * 의존성 추가하면 풀패키지 경로로 이 라이브러리가 있냐 판단으로 추가 가능
* 스프링 부트를 사용하는 경우 기본적으로 JacksonJSON2 라이브러리가 들어있다

### XML 컨버터 등록
* 의존성 라이브러리
  * JacksonXML
  * JAXB
* 스프링 부트를 사용하더라도 XML 라이브러리를 추가 해주지 않음
* 사용법은 백기선님 강좌에

## HTTP method(필요한 내용만 정리)
* GET
  * 캐싱 할 수 있다(조건적인 GET으로 바꿀수 있다)
  * 브라우저에 기록에 남는다
  * 북마크 할 수 있다
  * idemponent
    * 동일안 요청은 동일한 응답을 해야된다.
* POST
* PUT
* PATCH
  * idemponent
* DELETE

## URL 패턴
* 요청 식별자로 매핑 (중복 시 가장 디테일한 것으로 찾음)
  * ? 한글자 매핑 @GetMapping("/test/???") -> @GetMapping("/test/abc)
  * \* 여러 글자 @GetMapping("/test/*") -> @GetMapping("/test/abc")
  * \** 여러 패스 @GetMapping("/test/**") -> @GetMapping("/test/a/b/c")
  * 정규 표현식으로도 가능
* 스프링 MVC에서는 givejeong.html, givejeong.zip이 가능하다
  * 하지만 스프링 부트 부터는 이 기능을 지원하지 않는다 (RFD Attack 관련)
    * RFD attack 
      * 파일을 다운로드 받아지고 실행하면 보안상 위험

## 미디어 타입
* Content-Type
  * 요청할 때 이 형식으로 보낸다
  * @GetMapping(value="hello", consumers = MediaType.APPLICATION_JSON_UTF8_VALUE)
  * 매치가 안되면 415 에러
* Accept
  * 응답을 이런 형식으로 받고싶다
  * @GetMapping(value="hello", produces = MediaType.TEXT_PLAIN_VALUE)
  * 매치가 안되면 406에러

## 헤더와 매개 변수
* 특정한 헤더에 따라 처리하고 싶을 때
  * 특정 헤더가 있을 때
    * @GetMapping(header="key")
  * 특정 헤더가 없는 것만
    * @GetMapping(header="!key")
  * 특정 키와 값이 있는것
    * @GetMapping(header="key=value")
  * 파라미터가 있을 때
    * @GetMapping(header="key", params="a")
  * 파라미터가 없을 때
    * @GetMapping(header="key", params="!a")
  * 파라미터의 키와 값이 일치할 때
    * @GetMapping(header="key", params="key=a")

## HEAD OPTION
### HEAD
* GET 요청과 같지만 헤더만 보내준다. body 부분은 X

### OPTION
* uri의 메소드 정보를 확인할 수 있다.
  * /user라고 했을때 
    * GET : 유저 리스트
    * POST : 유저 생성
    * PUT, PATCH : 유저 수정
    * DELETE : 유저 삭제

## 커스텀 애노테이션
* [자바 애노테이션 관련 정리](https://github.com/jeonghyeonkwon/java-study/blob/main/src/main/java/com/jeonghyeon/javastudy/whiteship/liveStudy12/README.md)

### 메타 애노테이션
* 애노테이션에 사용할 수 있는 애노테이션
* 스프링에 제공하는 대부분 애노테이션들은 메타 에노테이션으로 사용할 수 있다.

### 애노테이션 만들어 보기
```java
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

@Documented // 문서화 시
@Target(ElementType.METHOD) // 어디에 이 애노테이션을 붙일 것인지
@Retention(RetentionPolicy.RUNTIME) // 언제까지 이 애노테이션을 살려놓을 것인지(런타입때까지 도)
@RequestMapping(value = "/hello", method = RequestMethod.GET)
public @interface GetHelloMapping {

}
```

## 메소드, 아규먼트
* 요청 또는 응답 자체에 접근 가능한 API
  * 스프링이 지원하는 아규먼트
    * WebRequest, WebNativeRequest
  * 서블릿이 지원하는 아규먼트
    * HttpServletRequest, HttpServletResponse
* 요청 본문, 응답 본문을 읽거나 쓸 때 사용하는 API
  * InputStream, Reader, OutputStream, Writer
* 스프링5, HTTP/2 리소스 푸쉬
  * PushBuilder
    * 요청 응답 후 보낼 리소스가 더있으면 요청을 보내지 않아도 응답을 보내줌(HTTP/2 기능)
* http method 정보를 알고 싶을 때
  * HttpMethod
* LocalResolver가 분석한 정보를 알고 싶을 때
  * Local, TimeZone, ZoneId
* ETC
  * @RequestBody, @CookieValue, @RequestHeader, @MatrixVariable

### @PathVariable
* option으로 required = true or false 사용 가능
* Optional<Long> id 같이 받는 형태도 가능
### @MatrixVariable
* key=value 쌍으로 받는 것
* 기본적으로 비활성화라서 풀어줘야됨
* url이 /event/1;name=jeonghyeon이라면 @PathVariable로 1을 받고 세미콜론 뒤의 name이 key jeonghyeon이 value로 받는다

## @Valid And @Validated
[@Valid 검증 관련 예제](https://github.com/jeonghyeonkwon/blog-example-project/tree/main/src/main/java/com/example/blogproject/aboutvalidate)
* Valid라는 애노테이션에는 그룹을 지정할 수 없다
  * Validated는 그룹으로 지정 가능하다

```java
import org.springframework.web.bind.annotation.ModelAttribute;

class User {
  interface MaxUser {
  }

  interface MinUser {
  }

  @Max(value = 0, groups = MaxUser.class)
  private Long MaxNum;

  @Min(value = 0, group = MinUser.class)
}


import org.springframework.validation.annotation.Validated;
        import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/user")
public ResponseEntity register(@Validated(User.MaxUser.class) @ModelAttribute ...){
        ...
}
```

## @SessionAttributes와 @SessionAttribute 차이

### @SessionAttributes
* 이 애노테이션은 전역으로 설정한다
* 이 애노테이션에 설정한 이름에 해당하는 모델 정보를 자동으로 세션에 넣어준다
* 여러 화면(또는 요청)에서 사용해야 하는 객체를 공유할 때 사용한다

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"event", ...})
public class AController {
  @GetMapping("/event/form")
  public String eventsForm(Model model){
    ...
      model.addAttribute("event", eventObject);// modelAttribute에 넣어줬지만 sessionAttributes에도 같은 키 임으로 session에도 넣어준다.
      return "/event/form";
  }
}
```
* SessionStatus를 사용해서 세션 처리 완료를 알려줄 수 있다
  * 세션 비울 때 사용
```java
@GetMapping("/event/form2")
public String eventsForm(SessionStatus sessionStatus){
        ...
        sessionStatus.setComplete();
        return "/event/form2";
}
```
### @SessionAttribute
* HttpSession을 사용해도 되지만 더 편리함
  * 넣고 빼고를 해야된다면 HttpSession을 사용하는 것이 낫다

```java

import org.springframework.web.bind.annotation.SessionAttribute;

@GetMapping("/")
public String method(@SessionAttribute("sessionKey") String sessionId){
// 들어 있는 세션의 값을 꺼낼 때 사용 (sessionId에 값이 담기고 sessionKey는 key이다)    
}

```
## @RedirectAttribute

* RedirectAttribute 설명 아님
  * spring-mvc에서는 model.addAttribute에 담은 값을 redirect 시킬 시 쿼리 파라미터로 붙는다( spring boot는 기본이 true다 )
```yaml
spring.mvc.ignore-default-model-on-redirect=false로 바꿔주면 됨
```

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("events/form/list")
public String register(Model model){
//    ...
        model.addAttribute("name", "jeonghyeon");
        return"redirect:/event/list";
}

// http://localhost:8080/event/list?name=jeonghyeon        
@GetMapping("/event/list")
...
```
* 전체 쿼리파라미터로 받고 싶지 않고 선택한 것만 받고 싶다? (스프링 부트에서는 위에 환경설정 지우고)
  * 그때 RedirectAttributes 사용하면 됨
```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("events/form/list")
public String register(RedirectAttributes attributes){
//    ...
        attributes.addAttribute("name", "jeonghyeon");
        return"redirect:/event/list";
}

// http://localhost:8080/event/list?name=jeonghyeon        
@GetMapping("/event/list")
public String getEvent()
// @RequestParam OR @ModelAttribute로 받으면 된다
```

## Flash Attributes
* RedirectAttribute에서 사용됨
* 기존 RedirectAttributes로 addAttribute할 시 url에 노출되지만 FlashAttribute로 사용하면 1회성 Http Session에 담겨서 전달된다.
  * 바로 삭제됨

```java
import org.springframework.web.bind.annotation.GetMapping;

@PostMapping("events/form/list")
public String register(RedirectAttributes attributes){
//    ...
        attributes.addFlashAttribute("name","jeonghyeon");
        return"redirect:/event/list";
        }

@GetMapping("/events/list")
public String getEvents(Model model, ...){
        String name = (String)model.asMap().get("name");
        ...
}

```

## 파일 업로드 - MultipartFile
* 파일 업로드시 사용하는 메소드 아규먼트
* MultipartResolver 빈 설정 되어 있어야 사용할 수 있다
  * 스프링 부트는 자동 설정
* POST multipart/form-data 요청에 들어있는 파일을 참조할 수 있다
* List 형태로도 받을수 있다

### 파일 업로드 테스트 코드 작성

```java
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

  @Autowired
  private MockMvc mockMvc;
  
  @Test
  public void fileUploadTest() throws Exception{
      MockMultipartFile file = new MockMultipartFile(
              "file",
              "test.txt",
              "text/plain",
              "hello file".getBytes()
      );
      this.mockMvc.perform(multipart("/file").file(file))
              .andDo(print())
              .andExpect(status().is3xxRedirection());
  }
}
```
## 파일 다운로드
* 파일 리소스 읽어 오기
  * 스프링 ResourceLoader 사용
* 파일 다운로드 헤더 설정
  * Content-Disposition : 사용자가 해당 파일을 받을 때 사용할 파일 이름
  * Content-Type : 어떤 파일인가
  * Content-Length: 얼마나 큰 파일인가
* 미디어 타입 알아내기
  * http://tika.apache.org/

```java

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GetMapping("/file/{filename}")
public ResponseEntity<Resource> downloadFile(
    @PathVariable String filename
) throws IOException {
    Resource resource = resourceLoader.getResource("classpath:"+filename);
    File file = resource.getFile();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,"attachement; filename=\"" + resource.getFilename()+"\"")
        .header(HttpHeaders.CONTENT_TYPE.type)
        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
        .body(resource);
}
```
## @RequestBody & HttpEntity
* RequestBody
  * 요청 본문에 들어있는 데이터를 HttpMessageConveter를 통해 변환한 객체로 받아올 수 있다
* HttpEntity
  * @RequestBody와 비슷하지만 추가적으로요청 헤더 정보를 사용할 수 있다.


### Json으로 컨버팅 하고 싶다
* 스프링 부트에서는 기본적으로 jackson2가 들어가 있다

## @ResponseBody & ResponseEntity
* ResponseBody는 기본적으로 @Restcontroller를 사용 시 붙여져 있다
* ResponseEntity
  * 응답 헤더 상태 코드 본문을 직접 다루고 싶은 경우에 사용한다.
## 출처
* [강좌 - 백기선님 스프링 MVC](https://www.inflearn.com/course/%EC%9B%B9-mvc)