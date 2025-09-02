package com.allra.allra.cart.service;

import com.allra.allra.cart.dto.CartDetailResponseDTO;
import com.allra.allra.cart.dto.CartItemRequestDTO;
import com.allra.allra.cart.entity.Cart;
import com.allra.allra.cart.entity.CartItem;
import com.allra.allra.cart.repository.CartItemRepository;
import com.allra.allra.cart.repository.CartRepository;
import com.allra.allra.product.entity.Product;
import com.allra.allra.product.entity.ProductStatus;
import com.allra.allra.product.repository.ProductRepository;
import com.allra.allra.user.entity.User;
import com.allra.allra.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    } // constructor

    /**
     *
     * @param userId
     * @param productId
     * @param quantity
     * @return
     */
    @Override
    @Transactional
    public void addItem(Long userId, Long productId, int quantity) {

        // ex) 재고 5개 남아있을 때 7개 들어오면 예외처리 해야하나? 우선 프론트에서 거를텐데..
        // 어차피 재고가 차감되는게 아니라 장바구니에 추가되고 장바구니 조회했을 때 재고부족이라고 들텐데
        // 애초에 예외처리로 걸러야하나? 장바구니 추가할 때?

        // 상품 재고 조회
        User user = getUserOrThrow(userId);
        Product product = getProductOrThrow(productId);

        // 상품의 상태 확인
        if(product.getStatus() != ProductStatus.FOR_SALE) {
            switch (product.getStatus()) {
                case SOLD_OUT:
                    throw new IllegalStateException("해당 상품은 품절되었습니다.");
                case DELETED:
                    throw new IllegalStateException("해당 상품은 판매가 중단되었습니다.");
                default:
                    throw new IllegalStateException("구매할 수 없는 상태의 상품입니다.");
            } // switch
        } // if

        // 요청 수량이 재고보다 많을 때
        if(product.getStock() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + product.getStock());
        } // if

        Cart cart = cartRepository.findWithItemsByUserIdForUpdate(user.getId())
                .orElseGet(() -> createCartSafely(user));

        cart.addOrIncrease(product, quantity, safeStock(product.getStock()));

    } // addItem

    /**
     * 사용자 ID를 이용하여 장바구니와 그 안에 담긴 상품들을 조회합니다.
     *  처음 장바구니에 상품을 담으면 새 장바구니를 생성함 (최초 로그인, 최초 장바구니 담기)
     *  장바구니와 연관된 CartItem, product 데이터 한 번에 조회
     * @param userId 사용자 Id
     * @return 장바구니에 등록된 상품들을 반환합니다. (장바구니ID, 사용자ID, 상품목록, 상품 가격, 상품 개수, 총 가격)
     */
    @Override
    public CartDetailResponseDTO findCartWithItems(Long userId) {

        // userId로 장바구니 있는지 확인, -> 없으면 생성
        // 그 조회된 장바구니에 들어있는 상품 확인
        // 장바구니에 들어있는 상품 조회할 때 N+1 문제가 발생할 수 있으므로 한번에 조회해야함
        User user = getUserOrThrow(userId);
        Cart cart = getCartOrCreate(user);

        // Cart와 CartItem은 1:N 관계이고 LAZY로 지연 로딩 하고있음..
        // 따라서 getItems()할 때 장바구니에 들어있는 상품들 가져오게 되면서 select * from products 를 여러번 실행하게 되므로 1+N문제가 터짐
        // 그래서 cart 조회할 때 애초에 상품들도 가져온 상태여야함
        // 이릃 해결하기 위해서 @EntityGraph 메서드 사용
        return CartDetailResponseDTO.from(cart);

    } // findCartWithItems

    /**
     * 장바구니의 특정 상품 수량을 지정된 값으로 설정합니다.
     *  같은 요청을 여러번 요청하더라도 장바구니 상태는 동일하게 유지(멱등)
     * @param userId        사용자ID
     * @param productId     상품ID
     * @param quantity      수량
     */
    @Override
    @Transactional
    public void setQuantity(Long userId, Long productId, int quantity) {

        // 사용자 조회
        // 장바구니에 상품 있는지 확인
        // 변경 수량이 상품의 재고 수량보다 높은 경우
        // 변경 수량이 -인 경우
        // 변경은 객체의 변경이니까 CartItem에 있는 entity에서 값을 변경해줘야한다.
        // 아직 주문한 상태는 아니니까 실제 상품의 재고를 변경할 필요는 없음
        // 이렇게 변경해놓고 누가 그 사이에 주문을 해서 재고가 변경된 quantity보다 적다면? 어떻게해야하지? 이건 동시성문제인가 어떤문제인가?
        // 이건 장바구니 결국 재조회 하면 거기서 재고와 상태가 반영되니 이 걸로 프론트에서 처리하면 됨

        User user = getUserOrThrow(userId);
        Cart cart = getCartOrCreate(user);
        CartItem cartItem = getCartItemOrThrow(cart.getId(), productId);

        Product product = cartItem.getProduct();
        int currentStock = product.getStock();

        cartItem.changeQuantity(quantity, currentStock);

    } // setQuantity

    /**
     * 장바구니에 담긴 특정 상품의 수량을 1씩 증가 또는 감소시킵니다. (비멱등)
     * @param userId        사용자ID
     * @param productId     상품ID
     * @param isIncrease      true - 증가, false - 감소
     */
    @Override
    @Transactional
    public void changeQuantityByProduct(Long userId, Long productId, boolean isIncrease) {

        // 사용자 조회
        // 장바구니에 상품 있는지 확인
        // 변경 수량이 상품의 재고 수량보다 높은 경우
        // 변경 수량이 -인 경우
        User user = getUserOrThrow(userId);
        Cart cart = getCartOrCreate(user);
        CartItem cartItem = getCartItemOrThrow(cart.getId(), productId);

        Product product = cartItem.getProduct();
        int currentStock = product.getStock();


        cartItem.chnageQuantityByOne(isIncrease, currentStock);

    } // changeQuantityByProduct

    /**
     * 장바구니에 담긴 상품을 모두 삭제합니다.
     * @param userId    사용자ID
     */
    @Override
    public void deleteCartItemsByUserId(Long userId) {

        User user = getUserOrThrow(userId);
        Cart cart = getCartOrCreate(user);

        cartItemRepository.deleteAll(cart.getItems());

    } // deleteCartItemsByUserId


    /**
     * 장바구니 특정 상품을 삭제합니다
     * @param userId        사용자ID
     * @param productId     상품ID
     */
    @Override
    @Transactional
    public void withdraw(Long userId, Long productId) {

        User user = getUserOrThrow(userId);
        Cart cart = getCartOrCreate(user);
        CartItem cartItem = getCartItemOrThrow(cart.getId(), productId);

        cartItemRepository.delete(cartItem);

    } // withdraw

    // ======================== 헬퍼메서드 ========================

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유효한 사용자가 아닙니다."));
    } // getUserOrThrow

    private Cart getCartOrCreate(User user) {
        return cartRepository.findWithItemsByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.from(user)));
    } // getCartOrCreate

    private CartItem getCartItemOrThrow(Long userId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(userId, productId)
                .orElseThrow(() -> new NoSuchElementException("장바구니에 해당 상품이 없습니다."));
    } // getCartItemOrThrow

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("유효한 상품이 아닙니다."));
    }

    private Cart createCartSafely(User user) {
        try {
            // flush를 통해 INSERT 쿼리가 DB로 바로 전송
            return cartRepository.saveAndFlush(Cart.from(user));
        } catch (DataIntegrityViolationException e) {
            // race: 다른 트랜잭션이 거의 동시에 같은 유저의 카트를 생성
            return cartRepository.findWithItemsByUserId(user.getId())
                    .orElseThrow(() -> new IllegalStateException("카트 생성 경합 이후 재조회 실패"));
        } // try-catch
    } // createCartSafely

    private int safeStock(Integer stock) {
        return stock == null ? 0 : stock;
    }

} // end class
