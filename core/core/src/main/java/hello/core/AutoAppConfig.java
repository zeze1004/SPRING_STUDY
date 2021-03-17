package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
// 자동으로 스프링 빈 등록
@ComponentScan(
        // 탐색할 패키지 시작 위치 지정
        basePackages = "hello.core.member",
        // 빈 등록 제외할 것들 설정
        // @Configuration이 붙은 설정 정보도 자동으로 등록되어 AppConfig도 빈으로 등록되어 제외
        // 보통 @Configuration를 컴포넌트 스캔에서 제외하지는 않지만 AppConfig에 bean을 일일이 등록해놨기 때문에 제외한 것
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class))

public class AutoAppConfig {

}
