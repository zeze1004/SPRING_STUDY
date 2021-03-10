package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class ApplicationContextInfoTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        // iter + tab: 반복문 자동 생성
        for (String beanDefinitionName : beanDefinitionNames) {
            // ac.getBean(): bean 이름으로 bean 객체(인스턴스) 조회
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name = " + beanDefinitionNames
            + " object = " + bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    // 내가 넣은 bean 출력
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        // iter + tab: 반복문 자동 생성
        for (String beanDefinitionName : beanDefinitionNames) {
            // getBeanDefinition(beanDefinitionName): bean의 정보를 얻을 수 있음
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            // BeanDefinition.ROLE_APPLICATION: 직접 등록한 애플리케이션 BEAN 혹은 외부 라이브러리리
            // BeanDefinition.ROLE_INFRASTRUCTURE: 스프링 내부에서 사용하는 빈
           if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
               Object bean = ac.getBean(beanDefinitionName);
               System.out.println("name = " + beanDefinitionNames
                       + " object = " + bean);
            }
        }

    }
}
