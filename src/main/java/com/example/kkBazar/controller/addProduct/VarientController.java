package com.example.kkBazar.controller.addProduct;

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

import com.example.kkBazar.entity.addProduct.Varient;
import com.example.kkBazar.service.addProduct.VarientService;

@RestController
@CrossOrigin(origins ="*")

public class VarientController {
	@Autowired
	private VarientService varientService;

	@GetMapping("/varient/view")
	public ResponseEntity<Object> getVarientDetails(@RequestParam(required = true) String varient) {
		if ("varientDetails".equals(varient)) {
			return ResponseEntity.ok(varientService.listAll());
		} else {
			String errorMessage = "Invalid value for 'varient'. Expected 'varientDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/varient/save")
	public ResponseEntity<String> saveAdminDetails(@RequestBody Varient varient) {
		try {
			varientService.SaveVarientDetails(varient);
			long id = varient.getVarientId();
			return ResponseEntity.ok("Varient Details saved successfully. Varient ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving varient: " + e.getMessage());
		}
	}

	@PutMapping("varient/edit/{id}")
	public ResponseEntity<Varient> updateOrder(@PathVariable("id") Long varient_id, @RequestBody Varient varient) {
		try {
			Varient existingVarient = varientService.findByVarientId(varient_id);

			if (existingVarient == null) {
				return ResponseEntity.notFound().build();
			}
			existingVarient.setVarientName(varient.getVarientName());
			existingVarient.setVarientLists(varient.getVarientLists());

			varientService.SaveVarientDetails(existingVarient);
			return ResponseEntity.ok(existingVarient);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/varient/delete/{id}")
	public ResponseEntity<String> deletVarient(@PathVariable("id") Long id) {
		varientService.deleteByVarient(id);
		return ResponseEntity.ok("Varient deleted successfully");
	}

}
