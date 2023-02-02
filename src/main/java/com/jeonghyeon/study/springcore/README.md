# 스프링 코어

## IOC(Inversion Of Control)
* 의존 관계 주입(Dependency Injection) 이라고도 함
* BeanFactory 인터페이스가 IOC의 핵심
  * ApplicationContext
    * BeanFactory를 상속받은 인터 페이스(자주 사용함)
    * EnvironmentCapable
    * ListableBeanFactory
    * HierarchicalBeanFactory
    * MessageSource
    * ApplicationEventPublisher
    * ResourcePatternResolver
* 빈 설정 소스로 부터 빈 정의를 읽어들이고, 빈을 구성하고 제공
* 기본 적으로 싱글톤으로 설정(프로토 타입도 설정 가능)
* 라이프사이클 인터페이스 지원
  * @PostConstruct
```java
AccountRepository repository = new AccountRepository();
AccountService sevice = new AccountService(repository);

//가 아닌

@Service
public class AccountService{
    @Autowired
    AccountRepository accountRepository;
}

```

## ApplicationContext
* 스프링 IOC 컨테이너는 빈 설정파일이 있어야 된다.
* XML, 자바 설정을 읽어 오는 방식이 있다
  * ClassPathXmlApplicationContext (XML)
  * AnnotationConfigApplicationContext (JAVA)
### 스프링 어노테이션

* @Bean
* @ComponentScan(basePackageClasses = 클래스명.class)
* @Component
  * @Controller
  * @Service
  * @Repository
  * @Configuration
## @Autowired
* required : 기본값 true

### 위치
* 생성자
* setter
* 필드

### 해당 타입의 빈이 여러개인 경우
* @Primary

```java
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AccountRepository implements Repository {

}
```
* @Qualifier
```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class AccountService {
  @Autowired
  @Qualifier("accountRepository")
  Repository repository;
}
```
* 다 받아라
```java
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountService {
  @Autowired
  List<Repository> repository;
}
```

* 필드명으로 주입받기
```java
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AccountService {
  @Autowired
  Repository accountRepository;
}
```

### 작동 원리
* BeanPostProcessor 인터페이스의 구현체에 의해서 동작


## @ComponentScan AND @Component

### @ComponentScan
* basePackage 기준으로 빈들을 스캔한다
  * 스프링 부트는 Application 클래스가 기본이다

#### basePackage 밖의 빈 등록하기

```java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public class SpApplication {
  @Autowired
  ExcludePackageClass clazz;

  public static void main(String[] args) {
    var app = new SpringApplication(SpApplication.class);

    app.addInitializers((ApplicationContextInitializer<GenericApplicationContext>)

            ctx -> {
              ctx.registerBean(ExcludePackageClass.class);
            });

    app.run(args);
  }
}
```


## 빈 스코프
* 기본은 싱글톤이다
  * 인스턴스가 1개만 생성되고 그 다음부터는 생성된 객체를 재활용 해서 쓴다는 것
* 프로토 타입의 예
  * Request
  * Session
  * WebSocket
  * ...
* 
### 빈 프로토 타입 만드는 방법

```java
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Proto{
  ...
}
```

### 빈 사이의 참조
* 프로토에서 싱글톤을 참조하면 문제가 되지 않는다.
* 하지만 싱글톤에서 프로토를 참조하면 프로토의 주소가 변하지 않는다
#### 싱글톤에서 참조하는 프로토의 객체 주소 변경시키는 방법
* proxyMode = ScopedProxyMode.TARGET_CLASS
  * 해당클래스를 프록시 클래스를 감싸서 싱글톤 객체가 프로토를 참조하는 것이 아닌 프록시를 참조
    * SingleTon -> Proxy -> Proto
```java
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Proto {
  ...
}
```


## Environment
* dev, stage, prod, ... 처럼 다른 환경에서 사용해야 되는 빈들을 모아두는 것
### 프로파일
* EnvironmentCapable의 제공 하는 기능 중 하나

```java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Configuration
@Profile("test")
public class TestConfiguration {
  @Bean
  //@Profile("test")
  public BoodRepository boodRepository() {
    return new BookRepository();
  }
}

//or
@Component
@Profile("test")
public interface BookRepository extends ...{
    
}

```
* -Dspring.profiles.active="test"

### 프로퍼티

* [스프링 부트 env](https://github.com/jeonghyeonkwon/spring-boot-study/blob/main/src/main/java/com/jeonghyeon/springbootstudy/read/uses.md)
* @PropertySource
  * Environment를 통해 프로퍼티 추가하는 방법


## MessageSource
* ApplicationContext는 MessageSource도 상속 받았다
* 국제화 기능을 제공한다
  * 언어별로 번역을 하여 내용을 반환한다
    * 번역기를 돌려주는 것이 아닌 직접 다 써야된다...
* 스프링 부트를 쓰고 있다면 파일명은 이렇게 지어야 된다
  * messages.properties
  * messages_ko_KR.properties

## ApplicationEventPublisher
* 이벤트 기반 프로그래밍에 필요한 인터페이스
  * 데이터를 전송하는 것과 그 이벤트를 구독하는 것
* 옵저버 패턴

```java
public class Event1 {
  private int data;
  private Object source;

  public Event1(Object source, int data) {
    this.source = source;
    this.data = data;
  }

  public int getData() {
    return data;
  }

  public Object getSource() {
    return source;
  }
}
```

```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Event1Handler {
  @EventListener
  public void handle(Event1 event){
    System.out.println("데이터는 " + event.getData());
  }
}
```

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {
  @Autowired
  ApplicationEventPublisher publisher;
  
  @Override
  public void run(ApplicationArguments args) throws Exception{
      publisher.publishEvent(new Event1(this,100));
  }

}
```
## ResourceLoader
* ApplicationContext는 ResourceLoader도 상속 받았다.
* ResourceLoader는 1개의 메소드만 있다
  * Resource getResource(String location);
* 리소스 읽어오기
  * 파일 시스템에서 읽어오기
  * 클래스패스에서 읽어오기
  * URL에서 읽어오기
  * 상대/절대 경로로 읽어오기

## Resource 
* 스프링 코어에서 제공하는 인터페이스
* Resource 타입은 ApplicationContext에 따라 결정됨
### 메소드들
* getInputStream()
* exits()
* isOpen()
* getDiscription()
### 구현체
* UrlResource
  * http, https, ftp, file, jar
* ClassPathResource
  * 기본 점두어 classpath:/
* FileSystemResource
* ServletContextResource
  * 웹 어플리케이션 루트에서 경로 찾음
* ...

### 명시적 접두어
* classpath:/
* file:///

## Validation 
* 검증용 인터페이스
* [Valid Validated 정리](https://github.com/jeonghyeonkwon/spring-mvc/blob/main/src/main/java/com/jeonghyeon/study/spring5/README.md)
* [@Valid 검증 관련 예제](https://github.com/jeonghyeonkwon/blog-example-project/tree/main/src/main/java/com/example/blogproject/aboutvalidate)
### 구현 인터페이스들
* boolean supports(Class class)
  * 어떤 타입의 객체를 검증할 때 사용할 것인지 결정함
* void validate(Object obj, Errors e)
  * 실제 검증 로직을 이 안에서 구현
    * 구현할 때 ValidationUtils

## PropertyEditor
* 웹에 특화된 기술은 아니지만 웹쪽으로 이해하자면 Mapping 받을때 "/event/{eventId}"로 요청 왔을 때 @PathVariable Event event 파라미터로 받도록 설정 가능
* 빈으로 등록해서 쓰면 안됨
  * 스레드-세이프 하지 않으므로 잘못 했다가는 1번회원이 2번회원 수정될 수 있다고 함
* Object <-> String 만 변환 가능
* DataBinder 사용
```java
import java.beans.PropertyEditorSupport;

public class EventEditor extends PropertyEditorSupport {
  @Override
  public String getAsText() {
    return ((Event) getValue()).getTitle(); 
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    setValue(new Event(Integer.parseInt(text)));
  }
}
```

```java
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AController {

  @InitBinder
  public void init(WebDataBinder binder){
      binder.registerCustomEditor(Event.class, new EventEditor);
  }
}
```

## Converter와 Formatter
### Converter
* A객체와 B객체 사이에 변환를 가능하게 해주는 것
* PropertyEditor와 달리 스레드-세이프 하다

* 컨버터 생성
```java
import org.springframework.core.convert.converter.Converter;

public class EventConverter {
    // A클래스를 B클래스로 변환
  public static class AToBConverter implements Converter<AClass, BClass> {
    @Override
    public BClass convert(AClass source) {
      //...
    }
  }
}
```
* 컨버터 등록 (@Component를 컨버터에 붙일시 스프링 부트에서는 자동이다)
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new EventConverter.AToBConverter());
  }
}
```
### Formatter
* [mvc 포메터](https://github.com/jeonghyeonkwon/spring-mvc/blob/main/src/main/java/com/jeonghyeon/study/spring5/README.md)
* PropertyEditor 대체제 (Object <-> String 변환)
  * 로케일(지역)에 따라 문자 처리 가능
  * 스레드-세이프 하다
* 포메터 생성 
```java
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class EventFormatter implements Formatter<AClass> {
  @Override
  public AClass parse(String text, Locale locale) throws ParseException {
    //...
  }

  @Override
  public String print(AClass object, Locale locale) {
    //...
  }
}
```

* 포메터 등록 (@Component를 포메터에 붙일시 스프링 부트에서는 자동이다)
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new EventFormatter());
  }
}
```
### ConversionService
* 실제 변환하는 작업이 스레드-세이프하는 이유는 이것 때문
  * PropertyEditor는 DataBinder 사용

### Test에서 Formatter, Converter
```java
@RunWith(SpringRunncer.class)
@WebMvcTest({EventConverter.AToBConverter.class, EventFormatter.class,...})
// 웹 에 필요한 컨버터나 포메터 등록 가능
```


## Null-safety
* NullPointException 방지

* @NonNull
  * ide에서 null을 주입시 경고 메시지를 준다(인텔리제이는 설정을 해야됨)
* @Nullable
  * ide에서 null을 주입시 경고 메시지를 준다(인텔리제이는 설정을 해야됨)

* @NonNullApi(패키지 레벨 설정)
  * 하위 패키지들에게도 적용 된다
* @NonNullFields(패키지 레벨 설정)
  * 하위 패키지들에게도 적용 된다


## AOP
* [만들었던 예제](https://github.com/jeonghyeonkwon/blog-example-project/blob/main/src/main/java/com/example/blogproject/aboutaop/aspect/AopAspect.java)
### Aspect
* 묶은 모듈

### Advice
* 해야할 일

### PointCut
* 어디에 적용하는지

### Target
* 적용이 되는 대상

### JoinPoint
* 시점 (실행할 때, 끝날 때, 접근 전, ...)


### 적용 방법
* 컴파일
  * AspectJ
* 로드 타임
  * AspectJ
* 런타임
  * 가장 현실적으로 합리적인 부분
  * 스프링 AOP

### 자바에서 AOP
* AspectJ
  * 더 다양한 기능을 제공 
* 스프링 AOP