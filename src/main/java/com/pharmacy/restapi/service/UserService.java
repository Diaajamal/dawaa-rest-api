package com.pharmacy.restapi.service;

import com.pharmacy.restapi.config.JwtService;
import com.pharmacy.restapi.config.SecurityConfiguration;
import com.pharmacy.restapi.model.*;
import com.pharmacy.restapi.payload.request.ProductRequest;
import com.pharmacy.restapi.payload.request.UserRequest;
import com.pharmacy.restapi.payload.response.CartProductResponse;
import com.pharmacy.restapi.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LogManager.getLogger(SecurityConfiguration.class);
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CartService cartService;
    private final CartProductService cartProductService;

    @Value("${jwt.token-prefix}")
    public String TOKEN_PREFIX;

    public void save(final User user) {
        userRepository.save(user);
    }

    public User findUser(final String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void addProductToCart(User user, Product product) {
        try {
            Cart cart = user.getCart();
            List<CartProduct> cartProducts = cart.getCartProducts();
            int productQuantity = product.getQuantity();
            if (!product.isInStock()) {
                logger.info("Product is out of stock!");
                return;
            }
            if (cartProducts.isEmpty()) {
                logger.info("Cart was empty, Adding the first product to cart");
                AddingNewProductToCart(user, product);
                product.setQuantity(productQuantity - 1);
                if (productQuantity - 1 <= 0) product.setInStock(false);
                return;
            }
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct.getProduct().getId().equals(product.getId())) {
                    logger.info("Product already in the cart, increasing quantity");
                    cartProduct.setQuantity(cartProduct.getQuantity() + 1);
                    cart.setQuantity(user.getCart().getQuantity() + 1);
                } else {
                    logger.info("Adding new Product to cart");
                    AddingNewProductToCart(user, product);
                }
                product.setQuantity(productQuantity - 1);
                if (productQuantity - 1 <= 0) product.setInStock(false);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void AddingNewProductToCart(User user, Product product) {
        Cart cart = user.getCart();
        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(1)
                .build();
        cart.getCartProducts().add(cartProduct);
        cart.setQuantity(cart.getQuantity() + 1);
        try {
            cartService.save(cart);
            cartProductService.save(cartProduct);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void deleteProductFromCart(User user, Product product) {
        try {
            List<CartProduct> cartProducts = user.getCart().getCartProducts();
            Cart cart = user.getCart();
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct.getProduct().getId().equals(product.getId())) {
                    cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                }
            }
            for (CartProduct cartProduct : cartProducts) {
                if (cartProduct.getQuantity() == 0) {
                    cartProducts.remove(cartProduct);
                    cartProductService.delete(cartProduct);
                }
            }
            cart.setQuantity(cart.getQuantity() - 1);
            int quantity = product.getQuantity();
            product.setQuantity(quantity + 1);
            if (quantity + 1 > 0) product.setInStock(true);
            cartService.save(cart);
            cartProductService.saveAll(cartProducts);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<CartProductResponse> getUserProducts(User user) {
        if (user == null) return null;
        try {
            List<CartProduct> cartProducts = user.getCart().getCartProducts();
            List<CartProductResponse> cartProductsResponse = new ArrayList<>();
            for (CartProduct cartProduct : cartProducts) {
                cartProductsResponse.add(CartProductResponse.builder()
                        .product(cartProduct.getProduct())
                        .quantity(cartProduct.getQuantity())
                        .total(cartProduct.getProduct().getPrice() * cartProduct.getQuantity())
                        .build());
            }
            return cartProductsResponse;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public User findUser(UUID id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (id == null) return;
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void createProduct(HttpServletRequest request, ProductRequest productRequest) {
        try {
            var seller = getUserFromToken(request);
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .price(productRequest.getPrice())
                    .description(productRequest.getDescription())
                    .imageUrl(productRequest.getImageUrl())
                    .quantity(productRequest.getQuantity())
                    .category(productRequest.getCategory())
                    .seller(seller.getId())
                    .inStock(true)
                    .build();
            productService.save(product);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void deleteProduct(HttpServletRequest request, UUID productId) {
        try {
            User seller = getUserFromToken(request);
            Product product = productService.findProduct(productId);
            if (product == null) throw new NullPointerException();
            if (!product.getSeller().equals(seller.getId())) throw new NullPointerException();
            productService.deleteProduct(productId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateProduct(UUID productId, ProductRequest updatedProduct) {
        try {
            if (updatedProduct == null || productId == null) throw new NullPointerException();
            Product oldProduct = productService.findProduct(productId);
            if (oldProduct == null) throw new NullPointerException();
            if (updatedProduct.getName() != null) {
                oldProduct.setName(updatedProduct.getName());
            }
            if (updatedProduct.getPrice() >= 0) {
                oldProduct.setPrice(updatedProduct.getPrice());
            }
            if (updatedProduct.getDescription() != null) {
                oldProduct.setDescription(updatedProduct.getDescription());
            }
            if (updatedProduct.getImageUrl() != null) {
                oldProduct.setImageUrl(updatedProduct.getImageUrl());
            }
            if (updatedProduct.getQuantity() >= 0) {
                oldProduct.setQuantity(updatedProduct.getQuantity());
            }
            if (updatedProduct.getCategory() != null) {
                oldProduct.setCategory(updatedProduct.getCategory());
            }
            if (oldProduct.getQuantity() == 0) {
                oldProduct.setInStock(false);
            }
            productService.save(oldProduct);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void updateUser(HttpServletRequest request, UserRequest updatedUser) {
        try {
            User oldUser = getUserFromToken(request);
            if (oldUser == null) throw new NullPointerException();
            if (updatedUser.getFirstName() != null)
                oldUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null)
                oldUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getUserName() != null)
                oldUser.setUserName(updatedUser.getUserName());
            if (updatedUser.getAddress() != null)
                oldUser.setAddress(updatedUser.getAddress());
            if (updatedUser.getPhone() != null)
                oldUser.setPhone(updatedUser.getPhone());
            if (updatedUser.getPassword() != null)
                oldUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            userRepository.save(oldUser);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public UserRequest getUser(HttpServletRequest request) {
        try {
            User user = getUserFromToken(request);
            if (user == null) throw new NullPointerException();
            return UserRequest.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUsername())
                    .address(user.getAddress())
                    .phone(user.getPhone())
                    .password(passwordEncoder.encode(user.getPassword()))
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    public User getUserFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace(TOKEN_PREFIX, "");
            String userName = jwtService.extractUserName(token);
            return userRepository.findByUserName(userName).orElse(null);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void addSeller(String userName) {
        try {
            User user = userRepository.findByUserName(userName).orElse(null);
            if (user != null) {
                user.setRole(Role.ADMIN);
                userRepository.save(user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<Product> getSellerProducts(HttpServletRequest request) {
        try {
            User seller = getUserFromToken(request);
            if (seller == null) return null;
            return productService.getBySellerId(seller.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Boolean checkOwnerShip(HttpServletRequest request, UUID productId) {
        try {
            User seller = getUserFromToken(request);
            if (seller == null) return false;
            Product product = productService.findProduct(productId);
            if (product == null) return false;
            return product.getSeller().equals(seller.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public void deleteSeller(String sellerUserName) {
        try {
            User seller = userRepository.findByUserName(sellerUserName).orElse(null);
            if (seller == null) return;
            seller.setRole(Role.USER);
            userRepository.save(seller);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
