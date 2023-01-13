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
* @Configuration
* @Bean
* @ComponentScan(basePackageClasses = 클래스명.class)
* @Component
  * @Controller
  * @Service
  * @Repository