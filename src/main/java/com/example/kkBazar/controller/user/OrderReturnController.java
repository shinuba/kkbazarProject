package com.example.kkBazar.controller.user;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.entity.user.OrderReturn;
import com.example.kkBazar.repository.user.OrderIemListRepository;
import com.example.kkBazar.repository.user.OrderReturnRepository;
import com.example.kkBazar.service.user.OrderReturnService;

@RestController
@CrossOrigin(origins ="*")

public class OrderReturnController {

	@Autowired
	private OrderReturnService orderReturnService;

	@Autowired
	private OrderReturnRepository orderReturnRepository;
	
	@Autowired
	private OrderIemListRepository orderItemListRepository;

	@GetMapping("/orderReturn/views")
	public ResponseEntity<?> getOrderReturnDetails(@RequestParam(required = true) String orderReturn) {
		try {
			if ("orderReturnDetails".equals(orderReturn)) {
				Iterable<OrderReturn> orderReturnDetails = orderReturnService.listAll();
				return new ResponseEntity<>(orderReturnDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderReturnDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderReturn details: " + e.getMessage());
		}
	}


	@PostMapping("/orderReturn/save")
	public ResponseEntity<Object> saveOrderReturnDetails(@RequestBody OrderReturn orderReturn) {

		try {
			orderReturn.setDate(LocalDate.now());
			orderReturn.setReturnStatus("return request is pending");
			orderReturnService.SaveOrderReturnDetails(orderReturn);
			OrderItemList orderItemList = orderItemListRepository.findById(orderReturn.getOrderItemListId())
					.orElse(null);
			if (orderItemList != null) {
				orderItemList.setOrderStatus("return request is pending");
				orderItemList.setReturnPending(true);
				orderItemList.setDelivered(false);
				orderItemListRepository.save(orderItemList);
			}
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Your Order return request submitted successfully.");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to submit order return request.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}



	
	@PatchMapping("/orderReturn/status/{orderReturnId}")
	public ResponseEntity<?> updateReturnStatus(@PathVariable("orderReturnId") Long orderReturnId,
	        @RequestBody OrderReturn requestBody) {

	    try {
	        OrderReturn existingReturn = orderReturnService.findById(orderReturnId);

	        if (existingReturn == null) {
	            return ResponseEntity.notFound().build();
	        }

	        String previousReturnStatus = existingReturn.getReturnStatus();
	        String newReturnStatus = requestBody.getReturnStatus();

	        existingReturn.setReturnStatus(newReturnStatus);

	        if ("accepted".equals(newReturnStatus)) {
	            existingReturn.setAccepted(true);
	            existingReturn.setRejected(false);

	        } else if ("rejected".equals(newReturnStatus)) {
	            existingReturn.setAccepted(false);
	            existingReturn.setRejected(true);

	        } else {
	            existingReturn.setAccepted(false);
	            existingReturn.setRejected(false);
	        }

	        orderReturnService.SaveOrderReturnDetails(existingReturn);

	        if (!previousReturnStatus.equals(newReturnStatus)) {
	            OrderItemList orderItemList = orderItemListRepository.findById(existingReturn.getOrderItemListId()).orElse(null);
	            if (orderItemList != null) {
	            	orderItemList.setOrderStatus("return " + newReturnStatus.toLowerCase());

	                if ("accepted".equals(newReturnStatus)) {
	                    orderItemList.setReturnAccepted(true);
	                    orderItemList.setReturnRejected(false);
	                    orderItemList.setReturnPending(false);
	                } else if ("rejected".equals(newReturnStatus)) {
	                    orderItemList.setReturnAccepted(false);
	                    orderItemList.setReturnRejected(true);
	                    orderItemList.setReturnPending(false);
	                } else {
	                    orderItemList.setReturnAccepted(false);
	                    orderItemList.setReturnRejected(false);
	                    orderItemList.setReturnPending(false);
	                }
	                orderItemListRepository.save(orderItemList);
	            }
	        }

	        return ResponseEntity.ok(existingReturn);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}


	@GetMapping("/userOrderReturnDetails")
	public ResponseEntity<Object> getUserReturnDetails1(@RequestParam(required = true) String orderReturn) {
		if ("orderReturnDetails".equals(orderReturn)) {
			try {
				List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetails();

				Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> action.get("date").toString(),
										Collectors.groupingBy(action -> action.get("order_return_id").toString()))));

				for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> dateList = new ArrayList<>();
					for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
							.entrySet()) {
						Map<String, Object> dateMap = new HashMap<>();
						dateMap.put("date", dateLoop.getKey());

						List<Map<String, Object>> orderReturnList = new ArrayList<>();
						for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
								.entrySet()) {
							Map<String, Object> orderReturnMap = new HashMap<>();
							orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());

							orderReturnMap.put("orderReturnId",
									orderReturnLoop.getValue().get(0).get("order_return_id"));
							orderReturnMap.put("orderItemListId",
									orderReturnLoop.getValue().get(0).get("order_item_list_id"));
							orderReturnMap.put("accepted", orderReturnLoop.getValue().get(0).get("accepted"));
							orderReturnMap.put("rejected", orderReturnLoop.getValue().get(0).get("rejected"));
							orderReturnMap.put("reasonForReturn",
									orderReturnLoop.getValue().get(0).get("reason_for_return"));
							orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
							orderReturnMap.put("productListId",
									orderReturnLoop.getValue().get(0).get("product_list_id"));
							orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
							orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

							orderReturnList.add(orderReturnMap);

						}

						dateMap.put("ListOfOrderReturnDetails", orderReturnList);
						dateList.add(dateMap);
					}

					userMap.put("userName", userRole.get(0).get("user_name"));
					userMap.put("userDetails", dateList);
					mainOrderReturnList.add(userMap);
				}

				return ResponseEntity.ok(mainOrderReturnList);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user order return details.");
			}
		} else {
			String errorMessage = "Invalid value for 'orderReturn'. Expected 'orderReturnDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/userOrderReturnDetail/{id}")
	public ResponseEntity<Object> getUserReturnDetails(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
			List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetailsByUserId(userId);

			Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
							Collectors.groupingBy(action -> action.get("date").toString(),
									Collectors.groupingBy(action -> action.get("order_return_id").toString()))));

			for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
					.entrySet()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userLoop.getKey());

				List<Map<String, Object>> dateList = new ArrayList<>();
				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
						.entrySet()) {
				Map<String, Object> dateMap = new HashMap<>();
					dateMap.put("date", dateLoop.getKey());

					List<Map<String, Object>> orderReturnList = new ArrayList<>();
					for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
							.entrySet()) {
						Map<String, Object> orderReturnMap = new HashMap<>();
						orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());

						orderReturnMap.put("orderItemListId",
								orderReturnLoop.getValue().get(0).get("order_item_list_id"));
						orderReturnMap.put("accepted", orderReturnLoop.getValue().get(0).get("accepted"));
						orderReturnMap.put("rejected", orderReturnLoop.getValue().get(0).get("rejected"));
						orderReturnMap.put("reasonForReturn",
								orderReturnLoop.getValue().get(0).get("reason_for_return"));
						orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
						orderReturnMap.put("productListId", orderReturnLoop.getValue().get(0).get("product_list_id"));
						orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
						orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

						orderReturnList.add(orderReturnMap);

					}
					dateMap.put("ListOfOrderReturnDetails", orderReturnList);
					dateList.add(dateMap);
				}

				userMap.put("userName", userRole.get(0).get("user_name"));
				userMap.put("userDetails", dateList);
				mainOrderReturnList.add(userMap);
			}

			return ResponseEntity.ok(mainOrderReturnList);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user order return details.");
		}
	}

	
	@GetMapping("/orderReturn/view")
	public ResponseEntity<?> getOrderReturnDetails1(@RequestParam(required = true) String orderReturn) {
		try {
			if ("orderReturnDetails".equals(orderReturn)) {
				Iterable<Map<String, Object>> orderReturnDetails = orderReturnRepository.getOrderReturnDetails();
				return new ResponseEntity<>(orderReturnDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderReturnDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderReturn details: " + e.getMessage());
		}
	}

	@GetMapping("order/return/details")
	public ResponseEntity<?> getReturnDetails1(@RequestParam(required = true) String orderReturn) {
		try {
			if ("returnDetails".equalsIgnoreCase(orderReturn)) {
				List<Map<String, Object>> returnDetails = orderReturnRepository.getReturnDetails();
				List<Map<String, Object>> orderMapList = new ArrayList<>();

				for (Map<String, Object> productDetails : returnDetails) {
					Map<String, Object> productMap = new HashMap<>();
					productMap.put("productVarientImageUrl", "varient/" + generateRandomNumber() + "/"
							+ productDetails.get("product_varient_images_id"));
					
					productMap.put("productId", productDetails.get("product_id"));
					productMap.put("productName", productDetails.get("product_name"));
					productMap.put("productListId", productDetails.get("product_list_id"));
					productMap.put("quantity", productDetails.get("quantity"));
					productMap.put("productVarientImagesId", productDetails.get("product_varient_images_id"));
					productMap.put("orderReturnId", productDetails.get("order_return_id"));
					productMap.put("returnStatus", productDetails.get("return_status"));
					productMap.put("reasonForReturn", productDetails.get("reason_for_return"));
					productMap.put("date", productDetails.get("date"));
					productMap.put("userId", productDetails.get("user_id"));
					productMap.put("userName", productDetails.get("user_name"));
					productMap.put("mobileNumber", productDetails.get("mobile_number"));
					productMap.put("alternateMobileNumber", productDetails.get("alternate_mobile_number"));
					productMap.put("totalPrice", productDetails.get("total_price"));
					orderMapList.add(productMap);
				}
				return ResponseEntity.ok(orderMapList);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	

	@GetMapping("/userOrderReturnDetails1")
	public ResponseEntity<Object> getUserReturnDetails11(@RequestParam(required = true) String orderReturn) {
		if ("orderReturnDetails".equals(orderReturn)) {
			try {
				List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetails();

				Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> action.get("date").toString(),
										Collectors.groupingBy(action -> action.get("order_return_id").toString()))));
				
				for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> dateList = new ArrayList<>();
					for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
							.entrySet()) {
						Map<String, Object> dateMap = new HashMap<>();
						dateMap.put("date", dateLoop.getKey());

						List<Map<String, Object>> orderReturnList = new ArrayList<>();
						for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
								.entrySet()) {
							Map<String, Object> orderReturnMap = new HashMap<>();

							orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());

							orderReturnMap.put("orderReturnId",
									orderReturnLoop.getValue().get(0).get("order_return_id"));
							orderReturnMap.put("orderItemListId",
									orderReturnLoop.getValue().get(0).get("order_item_list_id"));
							orderReturnMap.put("productId", orderReturnLoop.getValue().get(0).get("product_id"));
							orderReturnMap.put("productName", orderReturnLoop.getValue().get(0).get("product_name"));
							orderReturnMap.put("reasonForReturn",
									orderReturnLoop.getValue().get(0).get("reason_for_return"));
							orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
							orderReturnMap.put("productListId",
									orderReturnLoop.getValue().get(0).get("product_list_id"));
							orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
							orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

							orderReturnList.add(orderReturnMap);
						}
						dateMap.put("ListOfOrderReturnDetails", orderReturnList);
						dateList.add(dateMap);
					}
					userMap.put("userName", userRole.get(0).get("user_name"));
					userMap.put("mobileNumber", userRole.get(0).get("mobile_number"));
					userMap.put("alternateMobileNumber", userRole.get(0).get("alternate_mobile_number"));
					userMap.put("userDetails", dateList);
					mainOrderReturnList.add(userMap);
				}
				return ResponseEntity.ok(mainOrderReturnList);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user order return details.");
			}

		} else {
			String errorMessage = "Invalid value for 'orderReturn'. Expected 'orderReturnDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}


	@GetMapping("/orderReturnStatus/{id}")
	public List<Map<String, Object>> getReturnDetails(@PathVariable(value = "id") Long orderItemListId) {
		return orderReturnRepository.getReturnStatusById(orderItemListId);
	}


	@GetMapping("/orderReturnStatus/{userId}/{orderItemListId}")
	public List<Map<String, Object>> getReturnDetails(@PathVariable(value = "userId") Long userId,
			@PathVariable(value = "orderItemListId") Long orderItemListId) {
		return orderReturnRepository.getReturnStatusById(orderItemListId, userId);
	}
}
