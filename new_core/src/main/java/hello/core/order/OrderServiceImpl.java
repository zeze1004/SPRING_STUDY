package hello.core.order;


import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {   // 주문 생성
        Member member = memberRepository.findById(memberId);
        // 어떤 할인 정책을 가지는지 Order는 알 필요 없음
        // 할인 정책은 discount 도메인의 책임(단일 책임 원칙)
        // 최종 금액만 알면 됨
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
