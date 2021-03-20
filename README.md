# SPRING_STUDY
**개발 정리 노트 목차**



1. [스프링이란](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section1.md)

   자바 진영의 겨울과 봄이 온 이야기

2. [비즈니스 요구사항과 설계](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section2.md)

   - 주문, 할인 정책, 회원 도메일 설계

3. [변화되는 할인 정책을 스프링으로 쉽게 조작하기](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section3.md)

   - 기존 코드의 문제점:

      할인 정책 변경할 때마다 클라이언트 코드도 수정

   - 리팩토링 후:

     할인 정책(`DiscountPolicy` 인터페이스)를 상속 받을 구현 영역만 수정하면 됨

     클라이언트 코드인 `OrderServiceImpl`은 `DiscountPolicy`에 정률 할인 클래스인 `RateDiscountPolicy`이 들어올지 정률할인 클래스인 `FixDiscountPolicy`이 들어올지 알 수 없음

4. [본격적으로 스프링을 다뤄보자!](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section4.md)

   - 스프링 컨테이너란?

   - 스프링 빈의 개념과 조회법

     

5. [싱글톤 패턴](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section5.md)

   - 스프링 컨테이너의 기본 패턴인 싱글톤 패턴을 아라보자.

     싱글톤 패턴이란?

     클래스의 인스턴스가 1개만 생성되고 공유하는 패턴 

   - 싱글톤 패턴의 문제점

6. [ComponentScan을 사용해보자!](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section6.md)

   - `@ComponentScan`이란?

   - 나만의 컴포넌트 스캔 필터를 만들 수 있다는데...! 얼른 가서 확인해보자!

     `includeFilters`: `@ComponentScan` 사용시 `bean` 등록

     `excludeFilters`: `includeFilters`와 반대로 `bean` 등록 되지 않고 필터에 걸러짐.

7. [어의생(어차피 의존관계 주입법은 생성자 주입이란 뜻^^)](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section7.md)

   - 4가지 의존관계 주입 방법을 배워보자
   - 왜 생성자 주입을 써야하는지 알아보자

8. [BEAN 생명주기](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section8.md)

   - 빈이의 탄생과 죽음을 관장해보자

9. [다양한 BEAN 스코프](https://github.com/zeze1004/SPRING_STUDY/blob/master/%EB%85%B8%ED%8A%B8%ED%95%84%EA%B8%B0/section9.md)

   - 빈 스코프란?
   - 싱글톤 패턴과 양립되는 개념인 프로토타입 스코프에 대해 알아보자
   - 웹 스코프를 알아보자



