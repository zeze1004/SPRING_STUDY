package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    // 고정 할인 정책(FixDiscountPolicy())을 없애고 정률 할인 정책으로 바꿈
    // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    // 클라이언트 코드 OrderServiceImpl()가 인터페이스 DiscountPolicy()만 의존하도록 수정
    // OrderServiceImpl()이 discountPolicy 객체에 FixDiscountPolicy()를 직접 할당
    private DiscountPolicy discountPolicy = new FixDiscountPolicy();

    // OrderService와 discountPolicy가 분리되어 서로 영향x => 할인 정책 수정시 discountPolicy만 수정하면 됨
    // 단일 체계 원칙을 잘 지킴
   @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId);
        // 할인 정책에 적용할 수 있게 멤버와 아이템 가격 전달
        int discountPrice = discountPolicy.discount(member, itemPrice);

        // 주문 시 회원id, 주문 메뉴, 가격, 할인된 가격 반환
       return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
