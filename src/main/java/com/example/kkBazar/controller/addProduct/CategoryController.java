package com.example.kkBazar.controller.addProduct;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.example.kkBazar.entity.addProduct.Category;
import com.example.kkBazar.service.addProduct.CategoryService;

@RestController
@CrossOrigin(origins ="*")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/category/view")
	public ResponseEntity<Object> getCategoryDetails(@RequestParam(required = true) String category) {
		if ("categoryDetails".equals(category)) {
			return ResponseEntity.ok(categoryService.listCategory());
		} else {
			String errorMessage = "Invalid value for 'category'. Expected 'categoryDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/category/save")
	public ResponseEntity<String> saveCategoryDetails(@RequestBody Category category) {
		try {
			categoryService.SaveCateggoryDetails(category);
			long id = category.getCategoryId();
			return ResponseEntity.ok("Category Details saved successfully. Brand ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving category: " + e.getMessage());
		}
	}

	@PutMapping("/category/edit/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable("id") Long categoryId,
			@RequestBody Category category) {
		try {
			Category existingCategory = categoryService.findCategoryById(categoryId);

			if (existingCategory == null) {
				return ResponseEntity.notFound().build();
			}
			existingCategory.setCategoryName(category.getCategoryName());

			categoryService.SaveCateggoryDetails(existingCategory);
			return ResponseEntity.ok(existingCategory);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/category/delete/{id}")
	public ResponseEntity<String> deleteCategoryId(@PathVariable("id") Long categoryId) {
		categoryService.deleteCategoryById(categoryId);
		return ResponseEntity.ok("Category deleted successfully With Id :" + categoryId);

	}

	@PostMapping("/categoryImage/save")
	public ResponseEntity<String> saveCandidate(@RequestParam("categoryName") String categoryName,
			@RequestParam("categoryImage") MultipartFile categoryImage) throws SQLException {

		try {
			Category category = new Category();
			category.setCategoryName(categoryName);
			category.setCategoryImage(convertToBlob(categoryImage));

			categoryService.SaveCateggoryDetails(category);

			return ResponseEntity.ok("Category Details saved successfully.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while saving the category: " + e.getMessage());
		}
	}

	private Blob convertToBlob(MultipartFile file) throws IOException, SQLException{
		if (file !=null && !file.isEmpty()) {
			byte[] bytes = file.getBytes();
			return new javax.sql.rowset.serial.SerialBlob(bytes);
		}else {
			return null;
		}
	}

	@GetMapping("/category")
	public ResponseEntity<?> displayAllCategory(@RequestParam(required = true) String CategoryImage) {
		try {
			if ("CategoryDetail".equals(CategoryImage)) {
				List<Category> categoryList = categoryService.listCategory();
				List<Category> categoryObjects = new ArrayList<>();

				for (Category category : categoryList) {
					int randomNumber = generateRandomNumber();
					String fileExtension = getFileExtensionForImage(category);
					String imageUrl = "category/" + randomNumber + "/" + category.getCategoryId() + "." + fileExtension;

					Category categoryObject = new Category();
					categoryObject.setCategoryId(category.getCategoryId());
					categoryObject.setUrl(imageUrl);
					categoryObject.setCategoryName(category.getCategoryName());

					categoryObjects.add(categoryObject);
				}

				return ResponseEntity.ok().body(categoryObjects);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid categoryImage value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("category/{randomNumber}/{id:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		String fileExtension = parts[1];

		Long imageId;
		try {
			imageId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}

		Category image = categoryService.findCategoryById(imageId);
		if (image == null) {
			return ResponseEntity.notFound().build();
		}

		byte[] imageBytes;
		try {
			imageBytes = image.getCategoryImage().getBytes(1, (int) image.getCategoryImage().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		if ("jpg".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if ("png".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else {

			headers.setContentType(MediaType.IMAGE_JPEG);
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	private String getFileExtensionForImage(Category image) {
		if (image == null || image.getUrl() == null || image.getUrl().isEmpty()) {
			return "jpg";
		}
		String url = image.getUrl();
		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}

	@PutMapping("/categoryDetail/edit/{categoryId}")
	public ResponseEntity<String> updateCategory(@PathVariable long categoryId,
			@RequestParam(value = "categoryImage", required = false) MultipartFile file,
			@RequestParam(value = "categoryName", required = false) String categoryName) {
		try {
			Category category = categoryService.findCategoryById(categoryId);

			if (category == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
			}
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				category.setCategoryImage(blob);
			}
			if (categoryName != null) {
				category.setCategoryName(categoryName);
			}
			categoryService.SaveCateggoryDetails(category);

			return ResponseEntity.ok("Category updated successfully. Category ID: " + categoryId);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating category.");
		}
	}
}
