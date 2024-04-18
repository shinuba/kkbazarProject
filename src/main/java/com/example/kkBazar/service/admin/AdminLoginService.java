package com.example.kkBazar.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.admin.AdminLogin;
import com.example.kkBazar.repository.admin.AdminLoginRepository;



@Service
public class AdminLoginService {
	@Autowired
	private AdminLoginRepository adminLoginRepository;
	
	 public void addAdminLoginService() {
	    	
	   	 AdminLogin login = new AdminLogin();
	        login.setId(1);
	        login.setEmail("kkbazar.ideaux@gmail.com");
	        login.setPassword("ideaux@2411");
	        login.setName("kkbazar");
	        login.setRoleId(1);
	        login.setRoleType("Admin");

	        adminLoginRepository.save(login);
	   }
	
	 public boolean authenticateUser(String email, String password) {
	        AdminLogin user = adminLoginRepository.findByEmail(email); 
	        if (user != null) {
	        	 return compareRawPasswords(password, user.getPassword());
	        }
	        return false; 
	    }
	    
	    private boolean compareRawPasswords(String rawPassword, String storedPassword) {
	        return rawPassword.equals(storedPassword);
	    }
	    
	    
	    
	  //view
		public List<AdminLogin> listAll() {
			return this.adminLoginRepository.findAll();
		}

	//save
		public AdminLogin SaveAdminDetails(AdminLogin admin) {
			return adminLoginRepository.save(admin);
		}
		
		public AdminLogin findById(Long id) {
			return adminLoginRepository.findById(id).get();
		}
		
		 public AdminLogin findByEmail(String email) {
		        return adminLoginRepository.findByEmail(email);
		    }
}
