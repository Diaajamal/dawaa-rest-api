package com.pharmacy.restapi.service;

import com.pharmacy.restapi.model.Cart;
import com.pharmacy.restapi.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CartService {
   private final CartRepository cartRepository;
     public void save(Cart cart){
         cartRepository.save(cart);
     }
}