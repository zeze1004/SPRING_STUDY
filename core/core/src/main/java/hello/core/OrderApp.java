package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

public class OrderApp {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
        OrderService orderService = appConfig.orderService();

//        MemberService memberService = new MemberServiceImpl(null);
//        OrderService orderService = new OrderServiceImpl(null, null);

        Long memberId = 1L;
        Member member = new Member(memberId, "zeze", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(memberId, "love",10000);

        System.out.println("order = " + order); // toString으로 묶어놔서 개체 모두 출력된다
        System.out.println("order.calculatePrice() = " + order.calculatePrice()); // 할인된 가격
    }
}
