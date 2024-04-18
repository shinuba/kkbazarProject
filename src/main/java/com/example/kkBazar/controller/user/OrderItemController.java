package com.example.kkBazar.controller.user;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.user.OrderItem;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.entity.user.Review;
import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.repository.addProduct.ProductListRepository;
import com.example.kkBazar.repository.user.OrderItemRepository;
import com.example.kkBazar.repository.user.ReviewRepository;
import com.example.kkBazar.repository.user.UserAddressRepository;
import com.example.kkBazar.service.user.OrderItemService;

@RestController
@CrossOrigin(origins ="*")

public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ProductListRepository productListRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	@GetMapping("/orderItems/view")
	public ResponseEntity<?> getOrderItemDetails(@RequestParam(required = true) String orderItem) {
		try {
			if ("orderItemDetails".equals(orderItem)) {
				Iterable<OrderItem> orderItemDetails = orderItemService.listAll();
				return new ResponseEntity<>(orderItemDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided orderItem is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderItem details: " + e.getMessage());
		}
	}

	@PostMapping("/orderItems/save")
	public ResponseEntity<?> saveOrderItemsDetails(@RequestBody OrderItem orderItem) {
		try {
			LocalDate currentDate = LocalDate.now();

			orderItem.getOrderItemList().forEach(item -> item.setDate(currentDate));
			orderItem.setDate(currentDate);

			List<Long> orderItemIdList = orderItem.getOrderItemList().stream().map(OrderItemList::getProductListId)
					.collect(Collectors.toList());

			List<ProductList> productList = productListRepository.findAllById(orderItemIdList);

			for (ProductList product : productList) {
				for (OrderItemList orderItemItem : orderItem.getOrderItemList()) {
					if (product.getProductListId().equals(orderItemItem.getProductListId())) {
						product.setQuantity(Objects.nonNull(product.getQuantity())
								? product.getQuantity() - orderItemItem.getQuantity()
								: orderItemItem.getQuantity());
					}
				}
			}

			long userId = orderItem.getUserId();
			List<UserAddress> userDetailsList = userAddressRepository.findByUserId(userId);

			if (userDetailsList.isEmpty()
					|| userDetailsList.stream().noneMatch(user -> user.getStreetAddress() != null)) {
				Map<String, Object> ob = new HashMap<>();
				ob.put("message", "Address is not present");
				return ResponseEntity.badRequest().body(ob);
			}

			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				orderItemLoop.setOrderStatus("is pending");
			}

			productListRepository.saveAll(productList);
			orderItemService.SaveOrderItemDetails(orderItem);

			long id = orderItem.getUserId();

			for (OrderItemList orderItemLoop : orderItem.getOrderItemList()) {
				Review review = new Review();
				review.setUserId(id);
				review.setProductListId(orderItemLoop.getProductListId());
				review.setDate(currentDate);
				reviewRepository.save(review);
			}

			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("message", "Order confirmed successfully");
			return ResponseEntity.ok(orderMap);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product order details.");
		}
	}

	@Autowired
	private UserAddressRepository userAddressRepository;

	@GetMapping("/orderItems/detail/view/{id}")
	public ResponseEntity<Object> getOrderItemDetail(@PathVariable(value = "id") Long userId) {
		try {
			List<Map<String, Object>> mainDashboardList = new ArrayList<>();
			List<Map<String, Object>> orderItemRole = orderItemRepository.getOrderItemDetails(userId);

			Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderItemGroupMap = orderItemRole
					.stream()
					.collect(Collectors.groupingBy(action -> getString(action, "order_Item_id"), Collectors.groupingBy(
							action -> getString(action, "order_Item_List_Id"),
							Collectors.groupingBy(action -> getString(action, "product_List_Id"),
									Collectors.groupingBy(action -> getString(action, "product_Varient_Id"), Collectors
											.groupingBy(action -> getString(action, "product_Varient_Images_Id")))))));
			for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderLoop : orderItemGroupMap
					.entrySet()) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderItemId", orderLoop.getKey());
				List<Map<String, Object>> orderItemList = new ArrayList<>();
				for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> orderItemListLoop : orderLoop
						.getValue().entrySet()) {
					Map<String, Object> orderLoopProductListMap = new HashMap<>();
					orderLoopProductListMap.put("orderItemListId", orderItemListLoop.getKey());

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : orderItemListLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						productListMap.put("productListId", productListLoop.getKey());

						List<Map<String, Object>> variantList = new ArrayList<>();
						for (Map.Entry<String, Map<String, List<Map<String, Object>>>> variantLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> variantMap = new HashMap<>();
							variantMap.put("productVarientId", variantLoop.getKey());

							List<Map<String, Object>> productVarientImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> productVarientImagesLoop : variantLoop
									.getValue().entrySet()) {
								Map<String, Object> productVarientImageMap = new HashMap<>();
								String productVarientImagesId = productVarientImagesLoop.getKey();
								productVarientImageMap.put("productVarientImagesId", productVarientImagesLoop.getKey());
								Map<String, Object> firstItem = productVarientImagesLoop.getValue().get(0);

								orderMap.put("userId", firstItem.get("user_id"));
								orderMap.put("orderDate", firstItem.get("orderDate"));
								orderMap.put("totalPrice", firstItem.get("total_price"));
								orderMap.put("totalItems", firstItem.get("total_items"));
								orderLoopProductListMap.put("mrp",
										productVarientImagesLoop.getValue().get(0).get("mrp"));
								orderLoopProductListMap.put("buyRate",
										productVarientImagesLoop.getValue().get(0).get("buy_rate"));
								orderLoopProductListMap.put("sellRate",
										productVarientImagesLoop.getValue().get(0).get("sell_rate"));
								orderLoopProductListMap.put("discountPercentage",
										productVarientImagesLoop.getValue().get(0).get("discount_percentage"));
								orderLoopProductListMap.put("alertQuantity",
										productVarientImagesLoop.getValue().get(0).get("alert_quantity"));
								orderLoopProductListMap.put("gst",
										productVarientImagesLoop.getValue().get(0).get("gst"));
								orderLoopProductListMap.put("gstTaxAmount",
										productVarientImagesLoop.getValue().get(0).get("gst_tax_amount"));
								orderLoopProductListMap.put("totalAmount",
										productVarientImagesLoop.getValue().get(0).get("total_amount"));
								orderLoopProductListMap.put("productName",
										productVarientImagesLoop.getValue().get(0).get("product_name"));

								orderLoopProductListMap.put("unit",
										productVarientImagesLoop.getValue().get(0).get("unit"));
								orderLoopProductListMap.put("listDescription",
										productVarientImagesLoop.getValue().get(0).get("listDescription"));
								orderLoopProductListMap.put("productId",
										productVarientImagesLoop.getValue().get(0).get("product_id"));
								orderLoopProductListMap.put("orderItemQuantity",
										productVarientImagesLoop.getValue().get(0).get("orderItemQuantity"));
								orderLoopProductListMap.put("orderStatus",
										productVarientImagesLoop.getValue().get(0).get("order_status"));
								orderLoopProductListMap.put("productListId",
										productVarientImagesLoop.getValue().get(0).get("product_list_id"));

								int randomNumber = generateRandomNumber();
								String productVarientImageUrl = "varient/" + randomNumber + "/"
										+ productVarientImagesId;

								orderLoopProductListMap.put("productVarientImageUrl", productVarientImageUrl);

								productVarientImagesList.add(productVarientImageMap);
							}
							variantList.add(variantMap);
						}
						productList.add(productListMap);
					}
					orderItemList.add(orderLoopProductListMap);
				}
				orderMap.put("orderItemDetails", orderItemList);
				mainDashboardList.add(orderMap);
			}
			return ResponseEntity.ok(mainDashboardList);
		} catch (Exception e) {
			String errorMessage = "Error processing order item details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@GetMapping("/orderItemListDetails/{id}/{orderItemListId}")
	public List<Map<String, Object>> getAllOrderItemListDetails(@PathVariable("id") Long userId,
			@PathVariable("orderItemListId") Long orderItemListId) {
		List<Map<String, Object>> orderList = new ArrayList<>();
		List<Map<String, Object>> getOrderDetails = orderItemRepository.getAllOrderItemListDetails(userId,
				orderItemListId);
		Map<String, Object> orderMap = new HashMap<>();
		int randomNumber = generateRandomNumber();
		for (Map<String, Object> orderLoop : getOrderDetails) {
			String productVarientImageUrl = "varient/" + randomNumber + "/" + orderLoop.get("productVarientImagesId");
			orderMap.put("productVarientImageUrl", productVarientImageUrl);
			orderMap.putAll(orderLoop);

			orderList.add(orderMap);
		}
		return orderList;
	}
//	@GetMapping("/order/detail/{id}")
//	public List<Map<String, Object>> getOrderItemDetailss(@PathVariable("id") Long userId)
//			 {
//		List<Map<String, Object>> orderList = new ArrayList<>();
//		List<Map<String, Object>> getOrderDetails = orderItemRepository.getOrderItemDetails(userId);
//		Map<String, Object> orderMap = new HashMap<>();
//		int randomNumber = generateRandomNumber();
//		for (Map<String, Object> orderLoop : getOrderDetails) {
//			String productVarientImageUrl = "varient/" + randomNumber + "/" + orderLoop.get("productVarientImagesId");
//			orderMap.put("productVarientImageUrl", productVarientImageUrl);
//			orderMap.putAll(orderLoop);
//
//			orderList.add(orderMap);
//		}
//		return orderList;
//	}

	@GetMapping("/order/detail/{id}")
	public ResponseEntity<Object> getOrderItemDetails1(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> mainOrderList = new ArrayList<>();
			List<Map<String, Object>> userRole = orderItemRepository.getOrderItemDetails(userId);

			Map<String, List<Map<String, Object>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));

			List<Map.Entry<String, List<Map<String, Object>>>> sortedList = new ArrayList<>(userGroupMap.entrySet());
			sortedList.sort(
					(entry1, entry2) -> Long.compare(Long.parseLong(entry2.getKey()), Long.parseLong(entry1.getKey())));

			for (Map.Entry<String, List<Map<String, Object>>> orderLoop : sortedList) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderItemListId", orderLoop.getKey());

				List<Map<String, Object>> orderList = new ArrayList<>();
				for (Map<String, Object> orderItemMap : orderLoop.getValue()) {
					Map<String, Object> ordersMap = new HashMap<>();
					orderMap.put("orderItemId", orderItemMap.get("orderItemId"));
					orderMap.put("orderItemListId", orderItemMap.get("orderItemListId"));
					orderMap.put("cancelled", orderItemMap.get("cancelled"));
					orderMap.put("delivered", orderItemMap.get("delivered"));
					orderMap.put("orderStatus", orderItemMap.get("orderStatus"));
					orderMap.put("productListId", orderItemMap.get("productListId"));
					orderMap.put("quantity", orderItemMap.get("quantity"));
					orderMap.put("totalPrice", orderItemMap.get("totalPrice"));
					orderMap.put("productVarientImagesId", orderItemMap.get("productVarientImagesId"));
					orderMap.put("date", orderItemMap.get("date"));
					orderMap.put("totalItems", orderItemMap.get("totalItems"));
					orderMap.put("orderTotalPrice", orderItemMap.get("orderTotalPrice"));

					orderMap.put("userId", orderItemMap.get("userId"));
					orderMap.put("productId", orderItemMap.get("productId"));
					orderMap.put("totalAmount", orderItemMap.get("totalAmount"));
					orderMap.put("productName", orderItemMap.get("productName"));
					orderMap.put("description", orderItemMap.get("description"));
					orderMap.put("reviewId", orderItemMap.get("reviewId"));
					orderMap.put("starRate", orderItemMap.get("starRate"));
					orderMap.put("gst", orderItemMap.get("gst"));
					orderMap.put("mrp", orderItemMap.get("mrp"));
					orderMap.put("alertQuantity", orderItemMap.get("alertQuantity"));
					orderMap.put("discountPercentage", orderItemMap.get("discountPercentage"));
					orderMap.put("sellRate", orderItemMap.get("sellRate"));
					orderMap.put("unit", orderItemMap.get("unit"));
					orderMap.put("gstTaxAmount", orderItemMap.get("gstTaxAmount"));
					orderMap.put("buyRate", orderItemMap.get("buyRate"));
					orderMap.put("productVarientImageUrl",
							"varient/" + generateRandomNumber() + "/" + orderItemMap.get("productVarientImagesId"));
					orderList.add(ordersMap);
				}

				mainOrderList.add(orderMap);
			}

			return ResponseEntity.ok(mainOrderList);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user purchase details.");
		}
	}

	private String getString(Map<String, Object> map, String key) {
		Object value = map.get(key);
		return value != null ? value.toString() : "";
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/userPurchaseDetail/{id}")
	public ResponseEntity<Object> getUserPurchaseDetails(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> mainOrderList = new ArrayList<>();
			List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetailsByUserId(userId);

			Map<String, Map<String, List<Map<String, Object>>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
							Collectors.groupingBy(action -> action.get("date").toString())));

			for (Map.Entry<String, Map<String, List<Map<String, Object>>>> userLoop : userGroupMap.entrySet()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userLoop.getKey());

				List<Map<String, Object>> dateList = new ArrayList<>();
				for (Map.Entry<String, List<Map<String, Object>>> dateLoop : userLoop.getValue().entrySet()) {
					Map<String, Object> dateMap = new HashMap<>();
					dateMap.put("date", dateLoop.getKey());

					List<Map<String, Object>> orderList = new ArrayList<>();
					for (Map<String, Object> orderItemMap : dateLoop.getValue()) {
						Map<String, Object> orderMap = new HashMap<>();
						orderMap.put("orderItemId", orderItemMap.get("order_item_id"));
						orderMap.put("orderItemListId", orderItemMap.get("order_item_list_id"));
						orderMap.put("cancelled", orderItemMap.get("cancelled"));
						orderMap.put("delivered", orderItemMap.get("delivered"));
						orderMap.put("orderStatus", orderItemMap.get("order_status"));
						orderMap.put("productListId", orderItemMap.get("product_list_id"));
						orderMap.put("quantity", orderItemMap.get("quantity"));
						orderMap.put("totalPrice", orderItemMap.get("total_price"));

						orderList.add(orderMap);
					}

					dateMap.put("ListOfPurchaseOrder", orderList);
					dateList.add(dateMap);
				}

				userMap.put("userName", userRole.get(0).get("user_name"));
				userMap.put("userDetails", dateList);
				mainOrderList.add(userMap);
			}

			return ResponseEntity.ok(mainOrderList);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user purchase details.");
		}
	}

	@GetMapping("/getDashboardPageDetails")
	public ResponseEntity<?> getIncomeDetails1(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getDashboardPageDetails();
				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					String metric = (String) entry.get("metric");
					Object value = entry.get("value");
					if (shouldFormatAsDouble(metric)) {
						value = formatAsDouble(value);
					}
					result.put(metric, value);
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

	private boolean shouldFormatAsDouble(String metric) {
		return metric.equals("totalOrdersCurrentDay") || metric.equals("totalCustomers")
				|| metric.equals("totalOrdersCurrentYear") || metric.equals("totalOrdersCurrentMonth")
				|| metric.equals("totalCancelledOrders") || metric.equals("totalOrders")
				|| metric.equals("totalDeliveredOrders");
	}

	private Object formatAsDouble(Object value) {
		if (value instanceof Double && (Double) value % 1 == 0) {
			return ((Double) value).intValue();
		}
		return value;
	}

	@DeleteMapping("/order/delete/{orderItemId}")
	public ResponseEntity<String> deleteOrderDetail(@PathVariable("orderItemId") Long orderItemId) {
		orderItemService.deleteOrderItemId(orderItemId);
		return ResponseEntity.ok("orderItem details deleted successfully");
	}

	@GetMapping("/dashboardPageDetail")
	public ResponseEntity<?> getDashboardPageDetails(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getDashboardDetails();

				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					result.put((String) entry.get("metric"), entry.get("value"));
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

	@GetMapping("/incomeDetails")
	public ResponseEntity<?> getIncomeDetails(@RequestParam(required = true) String dashboard) {
		try {
			if ("dashboardPageDetail".equals(dashboard)) {
				List<Map<String, Object>> dashboardDetails = orderItemRepository.getIncomeDetails();

				Map<String, Object> result = new HashMap<>();
				for (Map<String, Object> entry : dashboardDetails) {
					result.put((String) entry.get("metric"), entry.get("value"));
				}
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving order details: " + e.getMessage());
		}
	}

// Find OrderList Details By CurrentDate
	@GetMapping("/orderListByCurrentDate")
	public ResponseEntity<?> findOrderListByCurrentDate(@RequestParam String order) {
		try {
			if ("orderListDetails".equals(order)) {
				List<Map<String, Object>> orderListDetails = orderItemRepository.findOrderListByCurrentDate();

				List<Map<String, Object>> modifiedOrderDetails = orderListDetails.stream().map(orderListDetail -> {
					Map<String, Object> result = new HashMap<>();
					result.put("orderItemId", orderListDetail.get("order_item_id"));
					result.put("date", orderListDetail.get("date"));
					result.put("userId", orderListDetail.get("user_id"));
					result.put("userName", orderListDetail.get("user_name"));
					result.put("orderItemListId", orderListDetail.get("order_item_list_id"));
					result.put("cancelled", orderListDetail.get("cancelled"));
					result.put("delivered", orderListDetail.get("delivered"));
					result.put("orderStatus", orderListDetail.get("order_status"));
					result.put("quantity", orderListDetail.get("quantity"));
					result.put("totalPrice", orderListDetail.get("total_price"));
					result.put("alertQuantity", orderListDetail.get("alert_quantity"));
					result.put("discountAmount", orderListDetail.get("discount_amount"));
					result.put("discountPercentage", orderListDetail.get("discount_percentage"));
					result.put("stockIn", orderListDetail.get("stock_in"));
					result.put("description", orderListDetail.get("description"));
					result.put("productQuantity", orderListDetail.get("product_quantity"));
					result.put("buyRate", orderListDetail.get("buy_rate"));
					result.put("unit", orderListDetail.get("unit"));
					result.put("gst", orderListDetail.get("gst"));
					result.put("gstTaxAmount", orderListDetail.get("gst_tax_amount"));
					result.put("mrp", orderListDetail.get("mrp"));
					result.put("sellRate", orderListDetail.get("sell_rate"));
					result.put("totalAmount", orderListDetail.get("total_amount"));
					result.put("productId", orderListDetail.get("product_id"));
					result.put("productName", orderListDetail.get("product_name"));
					result.put("productListId", orderListDetail.get("product_list_id"));
					result.put("productVarientId", orderListDetail.get("product_varient_id"));
					result.put("varientName", orderListDetail.get("varient_name"));
					result.put("varientValue", orderListDetail.get("varient_value"));

					result.put("productVarientImagesId", orderListDetail.get("product_varient_images_id"));
					result.put("productVarientImageUrl", "varient/" + generateRandomNumber() + "/"
							+ orderListDetail.get("product_varient_images_id"));
					return result;
				}).collect(Collectors.toList());

				return new ResponseEntity<>(modifiedOrderDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided orderList is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderList details: " + e.getMessage());
		}
	}

	@PostMapping("/findOrderListByDateRange")
	public ResponseEntity<List<Map<String, Object>>> findOrderListByDateRange1(
			@RequestBody Map<String, Object> requestBody) {
		if (!requestBody.containsKey("choose")) {
			return ResponseEntity.badRequest().build();
		}
		String choose = requestBody.get("choose").toString();
		switch (choose) {
		case "date":
			if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
				LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(),
						DateTimeFormatter.ISO_DATE);
				LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
				List<Map<String, Object>> orderList = orderItemRepository.findOrderListBetweenDate(startDate, endDate);
				List<Map<String, Object>> modifiedOrderDetails = new ArrayList<>();
				for (Map<String, Object> orderDetail : orderList) {
					int randomNumber = generateRandomNumber();
					String imageUrl = "varient/" + randomNumber + "/" + orderDetail.get("productVarientImagesId");
					Map<String, Object> modifiedDetail = new HashMap<>(orderDetail);
					modifiedDetail.put("productVarientImageUrl", imageUrl);
					modifiedOrderDetails.add(modifiedDetail);
				}
				return ResponseEntity.ok(modifiedOrderDetails);
			}
			break;
		case "month":
			if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
				String month = requestBody.get("monthName").toString();
				String year = requestBody.get("year").toString();
				List<Map<String, Object>> orderList = orderItemRepository.findOrderListByMonthYear(month, year);
				List<Map<String, Object>> modifiedOrderDetails = new ArrayList<>();
				for (Map<String, Object> orderDetail : orderList) {
					int randomNumber = generateRandomNumber();
					String imageUrl = "varient/" + randomNumber + "/" + orderDetail.get("productVarientImagesId");
					Map<String, Object> modifiedDetail = new HashMap<>(orderDetail);
					modifiedDetail.put("productVarientImageUrl", imageUrl);
					modifiedOrderDetails.add(modifiedDetail);
				}
				return ResponseEntity.ok(modifiedOrderDetails);
			}

			break;
		default:
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.badRequest().build();

	}

	@GetMapping("/orderDetails/view/{userId}/{productName}")
	public ResponseEntity<List<Map<String, Object>>> getOrderDetailByProductName(@PathVariable("userId") Long userId,
			@PathVariable("productName") String productName) {
		List<Map<String, Object>> orderDetails = orderItemRepository.getOrderDetailsByProductName(userId, productName);

		if (orderDetails.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		List<Map<String, Object>> response = orderDetails.stream().map(order -> {
			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("userId", order.get("user_id"));
			orderMap.put("orderItemId", order.get("order_item_id"));
			orderMap.put("totalPrice", order.get("totalPrice"));
			orderMap.put("totalItems", order.get("total_items"));
			orderMap.put("orderItemListId", order.get("order_item_list_id"));
			orderMap.put("listDescription", order.get("listDescription"));
			orderMap.put("orderItemQuantity", order.get("orderItemQuantity"));
			orderMap.put("orderStatus", order.get("order_status"));
			orderMap.put("date", order.get("date"));
			orderMap.put("productId", order.get("product_id"));
			orderMap.put("productName", order.get("product_name"));
			orderMap.put("productListId", order.get("product_list_id"));
			orderMap.put("buyRate", order.get("buy_rate"));
			orderMap.put("discountAmount", order.get("discount_amount"));
			orderMap.put("discountPercentage", order.get("discount_percentage"));
			orderMap.put("gst", order.get("gst"));
			orderMap.put("gstTaxAmount", order.get("gst_tax_amount"));
			orderMap.put("mrp", order.get("mrp"));
			orderMap.put("sellRate", order.get("sell_rate"));
			orderMap.put("alertQuantity", order.get("alert_quantity"));
			orderMap.put("productVarientImagesId", order.get("product_varient_images_id"));

			orderMap.put("productVarientImageUrl",
					"varient/" + generateRandomNumber() + "/" + order.get("product_varient_images_id"));

			return orderMap;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/sales/count/year")
	public List<Map<String, Object>> getAllCount(@RequestParam(required = true) String order) {
		try {
			if ("count".equalsIgnoreCase(order)) {
				return orderItemRepository.getOrderCounts();
			} else {
				throw new IllegalArgumentException("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@GetMapping("/orderCount/details")
	public List<Map<String, Object>> getAllOrderCounts(@RequestParam(required = true) String order) {
		try {
			if ("countDetails".equalsIgnoreCase(order)) {
				return orderItemRepository.getOrderDetails();
			} else {
				throw new IllegalArgumentException("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@GetMapping("/userPurchaseDetails")
	public ResponseEntity<Object> getAllPurchaseDetails(@RequestParam(required = true) String orderItem) {
		if ("orderItemDetails".equals(orderItem)) {
			try {
				List<Map<String, Object>> mainOrderList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetails();

				Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> userGroupMap = userRole
						.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> getString(action, "order_item_list_id"),
										Collectors.groupingBy(action -> getString(action, "product_varient_id"),
												Collectors.groupingBy(
														action -> getString(action, "product_varient_images_id"))))));

				for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> orderItemListList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> orderItemListLoop : userLoop
							.getValue().entrySet()) {
						Map<String, Object> orderItemListMap = new HashMap<>();
						orderItemListMap.put("orderItemListId", orderItemListLoop.getKey());

						List<Map<String, Object>> varientList = new ArrayList<>();
						List<Map<String, Object>> orderList = new ArrayList<>();
						for (Map.Entry<String, Map<String, List<Map<String, Object>>>> varientLoop : orderItemListLoop
								.getValue().entrySet()) {
							Map<String, Object> varientMap = new HashMap<>();
							varientMap.put("productVarientId", varientLoop.getKey());

							List<Map<String, Object>> varientImageList = new ArrayList<>();
							for (Map.Entry<String, List<Map<String, Object>>> varientImageLoop : varientLoop.getValue()
									.entrySet()) {
								Map<String, Object> varientImageMap = new HashMap<>();
								String productVarientImagesId = varientImageLoop.getKey();
								varientImageMap.put("productVarientImagesId", productVarientImagesId);

								for (Map<String, Object> orderItemMap : varientImageLoop.getValue()) {
									Map<String, Object> orderMap = new HashMap<>();

									orderMap.put("productName", orderItemMap.get("product_name"));
									orderItemListMap.put("orderItemId", orderItemMap.get("order_item_id"));
									userMap.put("orderTotalAmount", orderItemMap.get("orderTotalAmount"));
									orderItemListMap.put("orderItemListId", orderItemMap.get("order_item_list_id"));
									orderItemListMap.put("orderStatus", orderItemMap.get("order_status"));
									orderItemListMap.put("confirmed", orderItemMap.get("confirmed"));
									orderItemListMap.put("delivered", orderItemMap.get("delivered"));
									orderItemListMap.put("cancelled", orderItemMap.get("cancelled"));
									orderItemListMap.put("productName", orderItemMap.get("product_name"));
									orderItemListMap.put("productId", orderItemMap.get("product_id"));
									orderItemListMap.put("quantity", orderItemMap.get("quantity"));
									orderItemListMap.put("productListId", orderItemMap.get("product_list_id"));
									orderItemListMap.put("totalPrice", orderItemMap.get("total_price"));
									orderItemListMap.put("totalAmount", orderItemMap.get("total_amount"));
									varientMap.put("varientName", orderItemMap.get("varient_name"));
									varientMap.put("varientValue", orderItemMap.get("varient_value"));
									userMap.put("paymentType", orderItemMap.get("payment_type"));
									userMap.put("paymentStatus", orderItemMap.get("payment_status"));

									userMap.put("date", orderItemMap.get("date"));
									userMap.put("userName", orderItemMap.get("user_name"));
									userMap.put("mobileNumber", orderItemMap.get("mobile_number"));
									int randomNumber = generateRandomNumber();
									String productVarientImageUrl = "varient/" + randomNumber + "/"
											+ productVarientImagesId;
									varientImageMap.put("productVarientImageUrl", productVarientImageUrl);

									orderList.add(orderMap);
								}

								varientImageList.add(varientImageMap);
							}
							orderItemListMap.put("varientImages", varientImageList);
							varientList.add(varientMap);
						}
						orderItemListMap.put("varientValues", varientList);
						orderItemListList.add(orderItemListMap);
					}
					userMap.put("orderItemList", orderItemListList);
					mainOrderList.add(userMap);
				}

				return ResponseEntity.ok(mainOrderList);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user purchase details.");
			}
		} else {
			String errorMessage = "Invalid value for 'orderItem'. Expected 'orderItemDetails'.";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
	}

	@GetMapping("/userPurchaseDetails/demo")
	public ResponseEntity<Object> getAllPurchaseDetails6(@RequestParam(required = true) String orderItem) {
		if ("orderItemDetails".equals(orderItem)) {
			try {
				List<Map<String, Object>> mainOrderList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderItemRepository.getUserPurchaseDetails();

				Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> userGroupMap = userRole
						.stream()
						.collect(Collectors.groupingBy(action -> getString(action, "order_item_id"),
								Collectors.groupingBy(action -> getString(action, "order_item_list_id"),
										Collectors.groupingBy(action -> getString(action, "product_varient_id"),
												Collectors.groupingBy(
														action -> getString(action, "product_varient_images_id"))))));

				for (Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> orderGroupLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> orderItemMap = new HashMap<>();
					orderItemMap.put("orderItemId", orderGroupLoop.getKey());

					List<Map<String, Object>> orderItemList = new ArrayList<>();
					for (Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> orderItemGroupLoop : orderGroupLoop
							.getValue().entrySet()) {
						Map<String, Object> orderItemListMap = new HashMap<>();
						orderItemListMap.put("orderItemListId", orderItemGroupLoop.getKey());

						List<Map<String, Object>> varientList = new ArrayList<>();
						for (Entry<String, Map<String, List<Map<String, Object>>>> varientLoop : orderItemGroupLoop
								.getValue().entrySet()) {
							Map<String, Object> varientMap = new HashMap<>();
							varientMap.put("productVarientId", varientLoop.getKey());

							List<Map<String, Object>> varientImagesList = new ArrayList<>();
							for (Entry<String, List<Map<String, Object>>> varientImageLoop : varientLoop.getValue()
									.entrySet()) {
								Map<String, Object> varientImagesMap = new HashMap<>();
								varientImagesMap.put("productVarientImagesId", varientImageLoop.getKey());

								orderItemMap.put("productName", varientImageLoop.getValue().get(0).get("product_name"));
								orderItemMap.put("invoiceStatus",
										varientImageLoop.getValue().get(0).get("invoice_status"));
								orderItemMap.put("invoiceFlag", varientImageLoop.getValue().get(0).get("invoice_flag"));
								orderItemMap.put("orderTotalAmount", orderItemMap.get("orderTotalAmount"));
								orderItemListMap.put("orderStatus",
										varientImageLoop.getValue().get(0).get("order_status"));
								orderItemListMap.put("confirmed", varientImageLoop.getValue().get(0).get("confirmed"));
								orderItemListMap.put("delivered", varientImageLoop.getValue().get(0).get("delivered"));
								orderItemListMap.put("cancelled", varientImageLoop.getValue().get(0).get("cancelled"));
								orderItemListMap.put("productName",
										varientImageLoop.getValue().get(0).get("product_name"));
								orderItemListMap.put("productId", varientImageLoop.getValue().get(0).get("product_id"));
								orderItemListMap.put("quantity", varientImageLoop.getValue().get(0).get("quantity"));
								orderItemListMap.put("productListId",
										varientImageLoop.getValue().get(0).get("product_list_id"));
								orderItemListMap.put("totalPrice",
										varientImageLoop.getValue().get(0).get("total_price"));
								orderItemListMap.put("totalAmount",
										varientImageLoop.getValue().get(0).get("total_amount"));
								varientMap.put("varientName", varientImageLoop.getValue().get(0).get("varient_name"));
								varientMap.put("varientValue", varientImageLoop.getValue().get(0).get("varient_value"));
								orderItemMap.put("paymentType", varientImageLoop.getValue().get(0).get("payment_type"));
								orderItemMap.put("paymentStatus",
										varientImageLoop.getValue().get(0).get("payment_status"));

								orderItemMap.put("date", varientImageLoop.getValue().get(0).get("date"));
								orderItemMap.put("userName", varientImageLoop.getValue().get(0).get("user_name"));
								orderItemMap.put("mobileNumber",
										varientImageLoop.getValue().get(0).get("mobile_number"));
								int randomNumber = generateRandomNumber();
								String productVarientImagesId = varientImageLoop.getKey();
								String productVarientImageUrl = "varient/" + randomNumber + "/"
										+ productVarientImagesId;
								varientImagesMap.put("productVarientImageUrl", productVarientImageUrl);
								varientImagesList.add(varientImagesMap);
								orderItemListMap.put("varientImagesList", varientImagesList);
							}
							varientList.add(varientMap);
							orderItemListMap.put("varientList", varientList);
						}
						orderItemList.add(orderItemListMap);
						orderItemMap.put("orderItemListDetails", orderItemList);
					}
					mainOrderList.add(orderItemMap);
				}
				return ResponseEntity.ok(mainOrderList);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user purchase details.");
			}
		} else {
			String errorMessage = "Invalid value for 'orderItem'. Expected 'orderItemDetails'.";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		}
	}
}
