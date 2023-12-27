package hello.core.member;

public class MemberServiceImpl implements MemberService {

    // 추상 클래스인 MemberRepository와 구체 클래스인 MemoryMemberRepository를 모두 의존하고 있음
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
