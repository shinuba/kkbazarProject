package com.example.kkBazar.repository.companyProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.companyProfile.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


	@Query(value=" select i.invoice_id as invoiceId,i.company_id as CompanyId,i.order_item_id as orderItemId,c.address as companyAddress,"
			+ " c.company_name as companyName,c.country as companyCountry,"
			+ " c.email as companyEmail,pl.gst,pl.gst_tax_amount as gstTaxAmount,c.location as companyLocation,c.phone_number as companyPhoneNumber,"
			+ " c.pincode as companyPincode ,c.state as companyState,o.total_amount as totalAmount,o.total_items as totalItems,"
			+ " ol.order_item_list_id as orderItemListId,ol.product_list_id as productListId,ol.quantity,"
			+ " ol.total_price as totalPrice,ol.date as orderDate,"
			+ " o.user_id as userId, u.user_name as userName,u.email_id as userMail,"
			+ " u.mobile_number as userMobileNumber,a.user_address_id as userAddressId,"
			+ " a.address_type as addressType,a.city as userCity,a.country as userCountry,a.postal_code as userPostalCode,"
			+ " a.state as userState,a.street_address as userStreetAddress,p.product_id as productId,p.product_name as productName"
			+ " from invoice as i"
			+ " join company as c on c.company_id=i.company_id"
			+ " join order_item as o on o.order_item_id=i.order_item_id"
			+ " join order_item_list as ol on ol.order_item_id=o.order_item_id"
			+ " join product_list as pl on pl.product_list_id=ol.product_list_id"
			+ " join product as p on p.product_id=pl.product_id"
			+ " join user as u on u.user_id=o.user_id"
			+ " join user_address as a on a.user_id=u.user_id"
			+"  where ol.order_item_list_id = :order_item_list_id", nativeQuery = true)
	Map<String, Object> getInvoiceDetails(@Param("order_item_list_id") Long orderItemListId);	


	@Query(value = " select i.invoice_id as invoiceId,c.company_id as CompanyId,o.order_item_id as orderItemId,c.address as companyAddress,"
			+ "	c.company_name as companyName,c.country as companyCountry,o.date,"
			+ "	c.email as companyEmail,pl.gst,pl.gst_tax_amount as gstTaxAmount,"
			+ "  c.location as companyLocation,c.phone_number as companyPhoneNumber,"
			+ "	c.pincode as companyPincode ,c.state as companyState,o.total_amount as totalAmount,o.total_items as totalItems,"
			+ "	ol.order_item_list_id as orderItemListId,pl.product_list_id as productListId,ol.quantity,ol.total_amount,"
			+ "	ol.total_price as totalPrice,ol.date as orderDate,"
			+ "	o.user_id as userId, u.user_name as userName,u.email_id as userMail,"
			+ "	u.mobile_number as userMobileNumber,a.user_address_id as userAddressId,"
			+ "	a.address_type as addressType,a.city as userCity,a.country as userCountry,a.postal_code as userPostalCode,"
			+ "	a.state as userState,a.street_address as userStreetAddress,p.product_id as productId,p.product_name as productName"
			+ " from invoice as i" + "	join company as c on c.company_id=i.company_id"
			+ "	join order_item as o on o.order_item_id=i.order_item_id"
			+ "	join order_item_list as ol on ol.order_item_id=o.order_item_id"
			+ "	join product_list as pl on pl.product_list_id=ol.product_list_id"
			+ "	join product as p on p.product_id=pl.product_id" 
			+ "	join user as u on u.user_id=o.user_id"
			+ "	join user_address as a on a.user_id=u.user_id"
			+ " where ol.order_item_id = :order_item_id", nativeQuery = true)
	List<Map<String, Object>> getInvoiceDetailsByOrderItemId(@Param("order_item_id") Long orderItemId);

	
	Optional<Invoice> findByOrderItemId(long orderItemId);

}
