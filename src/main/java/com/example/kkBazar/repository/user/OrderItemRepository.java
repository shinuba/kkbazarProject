package com.example.kkBazar.repository.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,pv.product_varient_images_id as productVarientImagesId,pl.description,r.review_id as reviewId,r.star_rate as starRate,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity"
			+ " from order_item as o"
			+ " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_varient_images as pv on pv.product_list_id = pl.product_list_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " WHERE o.user_id = :user_id"
			+ " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getOrderItemDetails(@Param("user_id") Long userId);


	
	@Query(value="select ol.order_item_list_id as orderItemListId,ol.cancelled,ol.delivered,ol.date,ua.city,ua.country,ua.postal_code as postalCode,"
			+ " ua.state,ua.street_address as stateAddress,ua.address_type as addressType,u.user_name as userName,u.mobile_number as mobileNumber,"
			+ " ol.order_status as orderStatus,ol.product_list_id as productListId,p.product_name as productName,ol.quantity,ol.total_price as totalPrice,"
			+ " ol.total_amount as totalAmount,ol.order_item_id as orderItemId,o.user_id as userId,pl.mrp,pv.product_varient_images_id as productVarientImagesId,pl.description"
			+ " from order_item_list as ol"
			+ " join order_item as o on o.order_item_id = ol.order_item_id"
			+ " join user as u on u.user_id = o.user_id"
			+ " join user_address as ua on ua.user_id = u.user_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_varient_images as pv on pv.product_list_id=pl.product_list_id"
			+ " where o.user_id =:user_id and ol.order_item_list_id=:order_item_list_id", nativeQuery = true)
			List<Map<String, Object>> getAllOrderItemListDetails(Long user_id, Long order_item_list_id); 


	@Query(value = "SELECT u.user_id, u.user_name, o.order_item_id, o.date, ol.order_item_list_id, ol.cancelled, "
			+ "ol.delivered, ol.order_status, ol.product_list_id, ol.quantity, ol.total_price " + "FROM user AS u "
			+ "JOIN order_item AS o ON o.user_id = u.user_id "
			+ "JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id " + "WHERE u.user_id = :userId "
			+ "ORDER BY ol.order_item_list_id desc ", nativeQuery = true)
	List<Map<String, Object>> getUserPurchaseDetailsByUserId(Long userId);

	@Query(value = " SELECT 'totalOrders' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ " FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id UNION ALL"
			+ " SELECT 'totalOrdersCurrentDay' AS metric,COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE DATE(oi.date) = CURRENT_DATE UNION ALL"
			+ " SELECT 'totalOrdersCurrentMonth' AS metric,COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE)"
			+ " AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) UNION ALL SELECT 'totalOrdersCurrentYear' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ " FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " UNION ALL SELECT 'totalCustomers' AS metric, COUNT(u.user_id) AS value FROM user AS u UNION ALL SELECT"
			+ " 'totalDeliveredOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value FROM Order_item oi JOIN Order_item_list oii ON oi.order_item_id = oii.order_item_id "
			+ " WHERE oii.delivered = true UNION ALL SELECT 'totalCancelledOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value FROM"
			+ " Order_item oi JOIN Order_item_list oii ON oi.order_item_id = oii.order_item_id WHERE oii.cancelled = true UNION ALL"
			+ " SELECT 'currentDayIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value FROM Order_item_list oi"
			+ " WHERE DATE(oi.date) = CURRENT_DATE AND oi.delivered = true UNION ALL SELECT 'currentMonthIncome' AS metric,COALESCE(SUM(oi.total_price), 0) AS value"
			+ " FROM Order_item_list oi WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " AND oi.delivered = true UNION ALL SELECT 'currentYearIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value FROM Order_item_list oi"
			+ " WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true UNION ALL SELECT 'totalStock' AS metric,"
			+ " COALESCE(SUM(pl.quantity), 0) AS value FROM product_list pl", nativeQuery = true)
	List<Map<String, Object>> getDashboardPageDetails();



	@Query(value = " SELECT 'totalOrders' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id UNION ALL"
			+ " SELECT 'totalOrdersCurrentDay' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE DATE(oi.date) = CURRENT_DATE"
			+ " UNION ALL SELECT 'totalOrdersCurrentMonth' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ " FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id"
			+ " WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " UNION ALL SELECT 'totalOrdersCurrentYear' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " UNION ALL SELECT 'totalCustomers' AS metric, COUNT(u.user_id) AS value FROM user AS u"
			+ " UNION ALL SELECT 'totalDeliveredOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value"
			+ " FROM Order_item oi JOIN Order_item_list oii ON oi.order_item_id = oii.order_item_id"
			+ " WHERE oii.delivered = true UNION ALL SELECT 'totalCancelledOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value"
			+ " FROM Order_item oi JOIN Order_item_list oii ON oi.order_item_id = oii.order_item_id WHERE oii.cancelled = true", nativeQuery = true)
	List<Map<String, Object>> getDashboardDetails();

	@Query(value = " SELECT 'currentDayIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ " FROM Order_item_list oi WHERE DATE(oi.date) = CURRENT_DATE AND oi.delivered = true"
			+ " UNION ALL SELECT 'currentMonthIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ " FROM Order_item_list oi WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE)"
			+ " AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true"
			+ " UNION ALL SELECT 'currentYearIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ " FROM Order_item_list oi WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true"
			+ " UNION ALL SELECT 'totalStock' AS metric, COALESCE(SUM(pl.quantity), 0) AS value FROM product_list pl", nativeQuery = true)
	List<Map<String, Object>> getIncomeDetails();

	@Query(value = " SELECT o.order_item_id, o.date, u.user_id,u.user_name,ol.order_item_list_id,ol.cancelled,"
			+ " ol.delivered,ol.order_status,ol.quantity,ol.total_price,"
			+ " pl.product_list_id, pl.alert_quantity, pl.buy_rate, pl.discount_amount, pl.discount_percentage,"
			+ " pl.gst, pl.gst_tax_amount, pl.mrp, pl.sell_rate, pl.stock_in, pl.description, pl.total_amount, pl.quantity as product_quantity,"
			+ " pl.unit, p.product_id, p.product_name, pvi.product_varient_images_id,"
			+ " pvi.product_varient_image, pvi.product_varient_image_url, pv.product_varient_id,"
			+ " pv.varient_name, pv.varient_value" + " FROM user as u" + " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ " WHERE DATE(o.date) = CURRENT_DATE", nativeQuery = true)
	List<Map<String, Object>> findOrderListByCurrentDate();

	@Query(value = " SELECT o.order_item_id as orderItemId, o.date, u.user_id as userId,u.user_name as userName,ol.order_item_list_id as orderItemListId,ol.cancelled,"
			+ " ol.delivered,ol.order_status as orderStatus,ol.quantity,ol.total_price as totalPrice,"
			+ " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate, pl.discount_amount as discountmount, pl.discount_percentage as discountPercentage,"
			+ " pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, pl.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
			+ " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,"
			+ " pv.product_varient_id as productVarientId,"
			+ " pv.varient_name as varientName , pv.varient_value as varientValue" + " FROM user as u"
			+ " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ "   WHERE monthname(o.date)=:month" + "   AND year(o.date)=:year", nativeQuery = true)
	List<Map<String, Object>> findOrderListByMonthYear(@Param("month") String month, @Param("year") String year);


	@Query(value = " SELECT o.order_item_id as orderItemId, o.date, u.user_id as userId,u.user_name as userName,ol.order_item_list_id as orderItemListId,ol.cancelled,"
			+ " ol.delivered,ol.order_status as orderStatus,ol.quantity,ol.total_price as totalPrice,"
			+ " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate, pl.discount_amount as discountmount, pl.discount_percentage as discountPercentage,"
			+ "  pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, pl.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
			+ " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,"
			+ " pv.product_varient_id as productVarientId,"
			+ " pv.varient_name as varientName , pv.varient_value as varientValue" + " FROM user as u"
			+ " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ "  WHERE o.date BETWEEN :startDate AND :endDate", nativeQuery = true)
	List<Map<String, Object>> findOrderListBetweenDate(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query(value = " SELECT o.order_item_id, o.total_amount as totalPrice, o.total_items, o.user_id, u.user_name, "
			+ " oi.order_item_list_id, pl.product_list_id, pl.buy_rate, pl.sell_rate, pl.discount_amount, "
			+ " pl.discount_percentage, pl.alert_quantity, pl.gst, pl.gst_tax_amount, pl.mrp, pl.quantity, "
			+ " pl.total_amount, p.product_id, p.product_name, pl.unit,pv.varient_name, "
			+ " pv.varient_value, pl.description as listDescription, pvi.product_varient_image, "
			+ " pvi.product_varient_image_url, pv.product_varient_id, pvi.product_varient_images_id, "
			+ " oi.quantity as orderItemQuantity, oi.date, oi.order_status " + " FROM order_item as o "
			+ " JOIN order_item_list as oi ON oi.order_item_id = o.order_item_id "
			+ " JOIN user as u ON u.user_id = o.user_id "
			+ " JOIN product_list as pl ON pl.product_list_id = oi.product_list_id "
			+ " JOIN product as p ON p.product_id = pl.product_id "
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id "
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id "
			+ " WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName, '%'), ' ', ''))) "
			+ " AND o.user_id = :userId", nativeQuery = true)
	List<Map<String, Object>> getOrderDetailsByProductName(@Param("userId") Long userId,
			@Param("productName") String productName);

	@Query(value = " SELECT YEAR(CURDATE()) AS currentyear,YEAR(CURDATE()) - 1 AS previousyear,"
			+ " SUBSTRING(MONTHNAME(o.date), 1, 3) AS month,SUM(CASE WHEN YEAR(CURDATE()) = YEAR(o.date) THEN 1 ELSE 0 END) AS currentcount,"
			+ " SUM(CASE WHEN YEAR(CURDATE()) - 1 = YEAR(o.date) THEN 1 ELSE 0 END) AS previouscount"
			+ " FROM order_item_list AS o" + " WHERE YEAR(o.date) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1)"
			+ " GROUP BY SUBSTRING(MONTHNAME(o.date), 1, 3)" + " ORDER BY MIN(o.date)", nativeQuery = true)
	List<Map<String, Object>> getOrderCounts();

	@Query(value=" SELECT o.order_item_id, o.user_id, u.user_name, oi.order_item_list_id, pl.product_list_id, pl.buy_rate, "
	        + " pl.sell_rate, pl.discount_amount, pl.discount_percentage, pl.alert_quantity, pl.gst, pl.gst_tax_amount, "
	        + " pl.mrp, pl.quantity, pl.total_amount,p.product_id,p.product_name, pl.unit, "
	        + " pi.product_images_id, pv.varient_name, pv.varient_value, pl.description as listDescription, pv.product_varient_id, pvi.product_varient_images_id, "
	        + " oi.quantity as orderItemQuantity,oi.date, oi.order_status "
	        + " FROM order_item as o "
	        + " JOIN order_item_list as oi ON oi.order_item_id=o.order_item_id "
	        + " JOIN user as u ON u.user_id=o.user_id "
	        + " JOIN product_list as pl ON pl.product_list_id=oi.product_list_id "
	        + " JOIN product as p ON p.product_id=pl.product_id "
	        + " JOIN product_images as pi ON pi.product_id=p.product_id "
	        + " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id "
	        + " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id", nativeQuery = true)
	List<Map<String,Object>> getOrderItemDetails();

	
	 @Query(value=" SELECT year,totalOrders,deliveredCount,"
		 		+ " CAST(ROUND((deliveredCount / totalOrders) * 100, 0) AS UNSIGNED) AS deliveredPercentage,cancelledCount,"
		 		+ " CAST(ROUND((cancelledCount / totalOrders) * 100, 0) AS UNSIGNED) AS cancelledPercentage,"
		 		+ " CAST(ROUND((totalOrders / MAX(totalOrders) OVER ()) * 100, 0) AS UNSIGNED) AS totalOrderCountPercentage"
		 		+ " FROM(SELECT YEAR(o.date) AS year,COUNT(o.order_item_id) AS totalOrders,"
		 		+ " COUNT(CASE WHEN ol.order_status = 'delivered' THEN o.order_item_id END) AS deliveredCount,"
		 		+ " COUNT(CASE WHEN ol.order_status = 'cancelled' THEN o.order_item_id END) AS cancelledCount"
		 		+ " FROM order_item AS o JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
		 		+ " GROUP BY year) AS subquery"
		 		+ " ORDER BY year DESC", nativeQuery = true)
		List<Map<String,Object>>getOrderDetails();

	 
	 
	 @Query(value=" SELECT u.user_id, u.user_name,u.mobile_number,o.total_price as orderTotalAmount,p.product_id, o.order_item_id,o.date,ol.order_item_list_id,ol.cancelled,"
	 		+ "	 ol.delivered,ol.order_status,ol.confirmed,ol.product_list_id, ol.quantity, p.product_name,o.invoice_status,o.invoice_flag,"
	 		+ "	 ol.total_price,ol.total_amount,o.payment_type,va.product_varient_id,o.payment_status,pv.product_varient_images_id,"
	 		+ "  va.varient_name,va.varient_value"
	 		+ "	 FROM user AS u"
	 		+ "	  JOIN order_item AS o ON o.user_id = u.user_id"
	 		+ "	 JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
	 		+ "	 join product_list as pl on pl.product_list_id=ol.product_list_id"
	 		+ "	 join product as p on p.product_id=pl.product_id"
	 		+ "	 join product_varient_images as pv on pv.product_list_id=pl.product_list_id"
	 		+ "  join product_varient as va on va.product_list_id=pl.product_list_id"
	 		+ "	 ORDER BY  ol.order_item_list_id desc", nativeQuery = true)
	 List<Map<String, Object>> getUserPurchaseDetails();


}
