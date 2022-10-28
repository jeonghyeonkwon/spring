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
  * return 값이 void라도 @GetMappiing("/sample")이면 return을 sample로 뷰를 찾아줌 
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
            <param-value>com.jeonghyeon.study.WebConfig</param-value>
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
## 출처
* [강좌 - 백기선님 스프링 MVC](https://www.inflearn.com/course/%EC%9B%B9-mvc)