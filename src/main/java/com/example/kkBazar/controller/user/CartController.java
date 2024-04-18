package com.example.kkBazar.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.AddToCart;
import com.example.kkBazar.repository.user.CartRepository;
import com.example.kkBazar.service.user.CartService;

@RestController
@CrossOrigin(origins ="*")

public class CartController {
	@Autowired
	private CartService cartService;
	@Autowired
	private CartRepository cartRepository;

	@GetMapping("/cartDetails")
	public ResponseEntity<?> getUserCartDetails(@RequestParam(required = true) String addToCart) {
		try {
			if ("addToCartDetails".equals(addToCart)) {
				Iterable<AddToCart> cartDetails = cartService.listAll();
				return new ResponseEntity<>(cartDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided addToCart is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving cart details: " + e.getMessage());
		}
	}

//	@PostMapping("/cartDetails/save")
//	public ResponseEntity<?> saveUsercartDetails(@RequestBody AddToCart addToCart) {
//		try {
//			cartService.SaveCartDetails(addToCart);
//			long id = addToCart.getAddToCartId();
//			return ResponseEntity.status(HttpStatus.OK).body("Cart details saved successfully." + id);
//		} catch (Exception e) {
//			e.printStackTrace();
//			String errorMessage = "An error occurred while saving Cart details.";
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//		}
//	}

	@PostMapping("/cartDetails/save")
	public ResponseEntity<?> saveUsercartDetails(@RequestParam long productListId, @RequestParam long userId,
			@RequestParam long productVarientImagesId) {
		try {

			Optional<AddToCart> existingCart = cartService.findByUserIdAndProductListId(userId, productListId,
					productVarientImagesId);
			if (existingCart.isPresent()) {
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("message", "Item is already in Cart");
				return ResponseEntity.ok(successResponse);
			} else {
				AddToCart addToCart = new AddToCart();
				addToCart.setUserId(userId);
				addToCart.setProductListId(productListId);
				addToCart.setProductVarientImagesId(productVarientImagesId);
				cartService.SaveCartDetails(addToCart);
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("message", "Item saved to the cart successfully.");
				return ResponseEntity.ok(successResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to save or remove item from the cart.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	

	@DeleteMapping("/cart/delete/{addToCartId}")
	public ResponseEntity<Object> deleteCartDetail(@PathVariable("addToCartId") Long addToCartId) {
		try {
			cartService.deleteCartId(addToCartId);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Successfully removed item from your cart");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Item removal from the cart failed");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	
	@GetMapping("/cart")
	public List<Map<String, Object>> getDetail() {
		return cartRepository.getAllAddToCartDetails();
	}

	
	@GetMapping("/userAddToCartDetails")
	public List<Map<String, Object>> getCartDetails() {
		return cartRepository.getAllAddToCartDetails().stream().map(cartItem -> {
			Map<String, Object> result = new HashMap<>();

			result.put("addToCartId", cartItem.get("add_to_cart_id"));
			result.put("quantity", cartItem.get("quantity"));
			result.put("totalAmount", cartItem.get("total_amount"));
			result.put("productId", cartItem.get("product_id"));
			result.put("productName", cartItem.get("product_name"));
			result.put("productListId", cartItem.get("product_list_id"));
			result.put("buyRate", cartItem.get("buy_rate"));
			result.put("discountAmount", cartItem.get("discount_amount"));
			result.put("discountPercentage", cartItem.get("discount_percentage"));
			result.put("gst", cartItem.get("gst"));
			result.put("gstTaxAmount", cartItem.get("gst_tax_amount"));
			result.put("mrp", cartItem.get("mrp"));
			result.put("sellRate", cartItem.get("sell_rate"));
			result.put("alertQuantity", cartItem.get("alert_quantity"));
			result.put("productImagesId", cartItem.get("product_images_id"));
			result.put("productImagesUploadUrl",
					"product/" + generateRandomNumber() + "/" + cartItem.get("product_images_id"));
			result.put("productVarientId", cartItem.get("product_varient_id"));
			result.put("varientName", cartItem.get("varient_name"));
			result.put("varientValue", cartItem.get("varient_value"));
			result.put("productVarientImagesId", cartItem.get("product_varient_images_id"));
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + cartItem.get("product_varient_images_id"));
			result.put("categoryId", cartItem.get("category_id"));
			result.put("categoryName", cartItem.get("category_name"));
			result.put("url", "category/" + generateRandomNumber() + "/" + cartItem.get("category_id"));
			result.put("brandId", cartItem.get("brand_id"));
			result.put("brandName", cartItem.get("brand_name"));
			result.put("productDescriptionId", cartItem.get("product_description_id"));
			result.put("descriptionName", cartItem.get("description_name"));
			result.put("productDescriptionListId", cartItem.get("product_description_list_id"));
			result.put("name", cartItem.get("name"));
			result.put("value", cartItem.get("value"));
			result.put("userId", cartItem.get("user_id"));
			result.put("userName", cartItem.get("user_name"));
			return result;
		}).collect(Collectors.toList());
	}
	

	
	@GetMapping("/getAllCartDetailsByUserId/{id}")
	public ResponseEntity<List<Map<String, Object>>> getAllCartDetailsByUserId(@PathVariable("id") Long userId) {
		List<Map<String, Object>> cartDetails = cartRepository.getAllCartDetailsByUserId(userId);
		Map<String, List<Map<String, Object>>> cartGroupMap = cartDetails.stream()
				.collect(Collectors.groupingBy(action -> action.get("add_to_cart_id").toString()));
		List<Map<String, Object>> cartList = new ArrayList<>();
		for(Entry<String, List<Map<String, Object>>> cartItem : cartGroupMap.entrySet()) {
			Map<String, Object> result = new HashMap<>();
			result.put("addToCartId", cartItem.getValue().get(0).get("add_to_cart_id"));
			result.put("totalAmount", cartItem.getValue().get(0).get("total_amount"));
			result.put("productId", cartItem.getValue().get(0).get("product_id"));
			result.put("productName", cartItem.getValue().get(0).get("product_name"));
			result.put("productListId", cartItem.getValue().get(0).get("product_list_id"));
			result.put("buyRate", cartItem.getValue().get(0).get("buy_rate"));
			result.put("discountAmount", cartItem.getValue().get(0).get("discount_amount"));
			result.put("discountPercentage", cartItem.getValue().get(0).get("discount_percentage"));
			result.put("gst", cartItem.getValue().get(0).get("gst"));
			result.put("gstTaxAmount", cartItem.getValue().get(0).get("gst_tax_amount"));
			result.put("mrp", cartItem.getValue().get(0).get("mrp"));
			result.put("listDescription", cartItem.getValue().get(0).get("description"));
			result.put("sellRate", cartItem.getValue().get(0).get("sell_rate"));
			result.put("alertQuantity", cartItem.getValue().get(0).get("alert_quantity"));
			result.put("productVarientImagesId", cartItem.getValue().get(0).get("product_varient_images_id"));
			result.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + cartItem.getValue().get(0).get("product_varient_images_id"));
			result.put("userId", cartItem.getValue().get(0).get("user_id"));
			result.put("userName", cartItem.getValue().get(0).get("user_name"));
			cartList.add(result);
		}
		return ResponseEntity.ok(cartList);
	}
	
	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

}
