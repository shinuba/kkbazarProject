package com.example.kkBazar.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.service.user.OrderItemListService;

@RestController
@CrossOrigin(origins ="*")

public class OrderItemListController {

	@Autowired
	private OrderItemListService orderItemListService;

	@PatchMapping("/order/status/{orderItemListId}")
	public ResponseEntity<?> updateOrderStatus(@PathVariable("orderItemListId") Long orderItemListId,
			@RequestBody OrderItemList requestBody) {

		try {
			OrderItemList existingOrderItemList = orderItemListService.findById(orderItemListId);
			if (existingOrderItemList == null) {
				return ResponseEntity.notFound().build();
			}
			existingOrderItemList.setOrderStatus(requestBody.getOrderStatus());

			if ("delivered".equals(existingOrderItemList.getOrderStatus())) {
				existingOrderItemList.setDelivered(true);
				existingOrderItemList.setConfirmed(false);
			} else if ("confirmed".equals(existingOrderItemList.getOrderStatus())) {
				existingOrderItemList.setConfirmed(true);
				existingOrderItemList.setDelivered(false);
			}else {
				existingOrderItemList.setCancelled(false);
				existingOrderItemList.setConfirmed(false);
				existingOrderItemList.setDelivered(false);
			}
			orderItemListService.SaveOrderItemListDetails(existingOrderItemList);
			return ResponseEntity.ok(existingOrderItemList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PutMapping("/orderStatus/{orderItemListId}")
	public ResponseEntity<?> updateOrderStatus2(@PathVariable("orderItemListId") Long orderItemListId,
	        @RequestBody OrderItemList requestBody) {

	    try {
	        OrderItemList existingOrderItemList = orderItemListService.findById(orderItemListId);
	        if (existingOrderItemList == null) {
	            return ResponseEntity.notFound().build();
	        }

	        if ("cancelled".equals(existingOrderItemList.getOrderStatus())) {
	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("message", "Your order is already cancelled and cannot be changed.");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	        }
	        existingOrderItemList.setReason(requestBody.getReason());
	        existingOrderItemList.setOrderStatus(requestBody.getOrderStatus());

	        if ("cancelled".equals(existingOrderItemList.getOrderStatus())) {
	            existingOrderItemList.setCancelled(true);
	            existingOrderItemList.setDelivered(false);
	            existingOrderItemList.setConfirmed(false);
	        } else {
	            existingOrderItemList.setCancelled(false);
	        }

	        orderItemListService.SaveOrderItemListDetails(existingOrderItemList);

	        Map<String, Object> successResponse = new HashMap<>();
	        successResponse.put("message", "Your order cancelled successfully.");

	        return ResponseEntity.ok(successResponse);
	    } catch (Exception e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "Failed to order cancelled.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	}

	@PutMapping("/orderStatus/edit/{id}")
	public ResponseEntity<Map<String, Object>> updateOrderStatus1(
	        @PathVariable("id") Long orderItemListId,
	        @RequestBody OrderItemList orderItemList) {
	    try {
	        OrderItemList existingOrder = orderItemListService.findById(orderItemListId);

	        if (existingOrder == null) {
	            return ResponseEntity.notFound().build();
	        }

	        if (existingOrder.isCancelled()) {
	            Map<String, Object> alreadyCancelledResponse = new HashMap<>();
	            alreadyCancelledResponse.put("message", "Your order is already cancelled.");
	            return ResponseEntity.badRequest().body(alreadyCancelledResponse);
	        }
	        existingOrder.setCancelled(orderItemList.isCancelled());
	        orderItemListService.SaveOrderItemListDetails(existingOrder);
  
	        Map<String, Object> successResponse = new HashMap<>();
	        successResponse.put("message", "Your order cancelled successfully.");
	        
	        return ResponseEntity.ok(successResponse);
	    } catch (Exception e) {
	        Map<String, Object> errorResponse = new HashMap<>();
	        errorResponse.put("message", "Failed to order cancelled.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
}
}
