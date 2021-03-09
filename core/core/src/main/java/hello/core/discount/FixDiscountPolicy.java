package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;

public class FixDiscountPolicy implements DiscountPolicy {

    // 1000원 할인
    private int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        // VIP면 1000원 할인, 아니면 X
        if (member.getGrade() == Grade.VIP) {
            return  discountFixAmount;
        } else {
            return 0;
        }
    }
}
