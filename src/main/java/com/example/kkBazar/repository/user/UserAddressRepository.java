package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {


	@Query(value = "SELECT u.user_id as userId,u.user_name as userName,u.mobile_number as mobileNumber,a.address_type as addressType, a.user_address_id AS userAddressId, a.city, a.country, a.postal_code AS postalCode, a.state, a.street_address AS streetAddress "
			+ " FROM user AS u " + "JOIN user_address AS a ON u.user_id = a.user_id "
			+ " WHERE u.user_id = :user_id and a.address_type=:address_type", nativeQuery = true)
	List<Map<String, Object>> getUserAddressTypeDetails(@Param("user_id") Long userId,
			@Param("address_type") String addressType);

	List<UserAddress> findByUserId(long userId);

}
