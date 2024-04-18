package com.example.kkBazar.controller.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.repository.user.UserAddressRepository;
import com.example.kkBazar.service.user.UserAddressService;

@RestController
@CrossOrigin(origins ="*")
public class UserAddressController {

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@GetMapping("/address/view")
	public ResponseEntity<?> getAddressDetails(@RequestParam(required = true) String address) {
		try {
			if ("addressDetails".equals(address)) {
				Iterable<UserAddress> addressDetails = userAddressService.listAll();
				return new ResponseEntity<>(addressDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided address is not supported.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving address details: " + e.getMessage());
		}
	}

	@PostMapping("/address/save")
	public ResponseEntity<Object> saveAddressDetails(@RequestBody UserAddress address) {
		try {
			Long id = address.getUserAddressId();
			if (id == 0 || id == null) {
				userAddressService.saveAddressDetails(address);
			} else {
				userAddressService.saveAddressDetails(address);
			}

			return ResponseEntity.ok("Address Details saved successfully. UserAddress ID" + id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving address: " + e.getMessage());

		}
	}

	@PutMapping("/address/edit/{id}")
	public ResponseEntity<Object> updateAddress(@PathVariable("id") Long userAddressId,
			@RequestBody UserAddress address) {
		try {
			UserAddress existingAddress = userAddressService.findAddressById(userAddressId);

			if (existingAddress == null) {
				return ResponseEntity.notFound().build();
			}

			existingAddress.setCity(address.getCity());
			existingAddress.setCountry(address.getCountry());
			existingAddress.setPostalCode(address.getPostalCode());
			existingAddress.setState(address.getState());
			existingAddress.setStreetAddress(address.getStreetAddress());
			existingAddress.setAddressType(address.getAddressType());
			userAddressService.saveAddressDetails(existingAddress);

			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Address details updated successfully.");

			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to update address details.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@DeleteMapping("/address/delete/{userAddressId}")
	public ResponseEntity<Object> deleteAddressId(@PathVariable("userAddressId") Long userAddressId) {
		try {
			userAddressService.deleteUserAddressById(userAddressId);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Address deleted successfully");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to delete address");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	
	
	@GetMapping("/userWithAddress/detail/{id}/{addressType}")
	public ResponseEntity<List<Map<String, Object>>> getUserWithAddressTypeDetails(
	        @PathVariable("id") Long userId,
	        @PathVariable("addressType") String addressType) {
	    try {
	        List<Map<String, Object>> result = userAddressRepository.getUserAddressTypeDetails(userId, addressType);

	        if (result == null) {
	            return ResponseEntity.ok(Collections.emptyList()); 
	        }
	        return ResponseEntity.ok(result);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	@GetMapping("/userWithAddress/{id}/{addressType}")
	public List<Map<String, Object>> getUserWithAddressTypeDetails1(@PathVariable("id") Long userId,
			@PathVariable("addressType") String addressType) {
		return userAddressRepository.getUserAddressTypeDetails(userId, addressType);
	}
}



