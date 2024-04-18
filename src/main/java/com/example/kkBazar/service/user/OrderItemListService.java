package com.example.kkBazar.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.repository.user.OrderIemListRepository;

@Service
public class OrderItemListService {

	@Autowired
	private OrderIemListRepository orderIemListRepository;
	
	//save
			public OrderItemList SaveOrderItemListDetails(OrderItemList orderItemList) {
				return orderIemListRepository.save(orderItemList);
			}

			// edit
			public OrderItemList findById(Long orderItemListId) {
				return orderIemListRepository.findById(orderItemListId).get();
			}
}
