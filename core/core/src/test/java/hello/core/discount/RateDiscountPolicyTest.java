package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateDiscountPolicyTest {
    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();
    // 성공 케이스
    @Test
    @DisplayName("vip는 10% 할인이 적용되어야 함")
    void vip_o() {
        // given
        Member member = new Member(1L, "zeze", Grade.VIP);
        // when
        int discount = discountPolicy.discount(member, 10000);
        // then
        Assertions.assertThat(discount).isEqualTo(1000);
    }
    // 실패 케이스
    @Test
    @DisplayName("VIP가 아니면 할인이 적용되지 않아야 함")
    void vip_x() {
        // given
        Member member = new Member(1L, "zezeBASIC", Grade.BASIC);
        // when
        int discount = discountPolicy.discount(member, 10000);
        // then
        Assertions.assertThat(discount).isEqualTo(0);    // BASIC 등급은 discount가 0이어야 함
    }

}