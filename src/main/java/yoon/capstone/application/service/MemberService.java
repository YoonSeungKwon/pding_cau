package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yoon.capstone.application.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;



}
