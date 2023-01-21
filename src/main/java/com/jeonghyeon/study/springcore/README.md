# 스프링 코어

## IOC(Inversion Of Control)
* 의존 관계 주입(Dependency Injection) 이라고도 함
* BeanFactory 인터페이스가 IOC의 핵심
  * ApplicationContext
    * BeanFactory를 상속받은 인터 페이스(자주 사용함)
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
