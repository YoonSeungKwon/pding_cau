package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yoon.capstone.application.repository.CartRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;


}
