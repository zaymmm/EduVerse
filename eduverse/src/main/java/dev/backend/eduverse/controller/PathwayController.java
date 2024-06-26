/*
 * @Author : Alvin
 * @Date : 5/11/2024
 * @Time : 9:00 PM
 * @Project_Name : eduverse
*/
package dev.backend.eduverse.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.backend.eduverse.dto.PathwayDTO;
import dev.backend.eduverse.exception.NameAlreadyExistException;
import dev.backend.eduverse.service.PathwayService;
import dev.backend.eduverse.util.ResponeTemplate.ApiResponse;
import dev.backend.eduverse.util.ResponeTemplate.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Tag(
        name = "CRUD REST APIs for Pathway",
        description = "CRUD REST APIs - Create Pathway, Update Pathway, Get All Pathways, Delete Pathway"
)
@RestController
@RequestMapping("/api/pathway")
public class PathwayController {
	private final Logger logger = LoggerFactory.getLogger(PathwayController.class);

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@Autowired
	private final PathwayService pathwayService;

	public PathwayController(PathwayService pathwayService) {
		this.pathwayService = pathwayService;
	}
	
	@PostMapping("/create")
	@Operation(summary = "Create a new pathway", tags = { "Pathway Creator" })
	public ResponseEntity<ApiResponse<String>> createPathway(@Valid @RequestBody PathwayDTO pathwayDTO) {
	    try {
	        boolean created = pathwayService.createPathway(pathwayDTO);
	        if (created) {
	            return ResponseUtil.createSuccessResponse(HttpStatus.OK, "Pathway created successfully", "created");
	        } else {
	            return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create pathway", "Creation failed due to unknown reasons");
	        }	    	     	   
	    } catch (NameAlreadyExistException e) {
	        String errorMessage = "Pathway with name '" + pathwayDTO.getName() + "' already exists";
	        return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create pathway", errorMessage);
	    } 	    
	    catch (DataIntegrityViolationException e) {
	        return ResponseUtil.createErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create pathway", e.getMessage());	       
	    } catch (Exception e) {
	    	logger.error("Failed to create pathway", e);
	        return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create pathway", e.getMessage());
	    }
	}
	
	@GetMapping("/read")
	@Operation(summary = "Retrieve all pathways", tags = { "Pathway Reader" })
	public ResponseEntity<ApiResponse<List<PathwayDTO>>> readPathways() {
	    try {
	        List<PathwayDTO> pathwayList = pathwayService.getAllPathway();
	        if (pathwayList.isEmpty()) {
	            return ResponseUtil.createSuccessResponse(HttpStatus.OK, "No pathways found", new ArrayList<>());
	        } else {
	            return ResponseUtil.createSuccessResponse(HttpStatus.OK, "Pathways retrieved successfully", pathwayList);
	        }
	    } catch (Exception e) {
	    	logger.error("Failed to retrieve pathways", e);
	        return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve pathways", null);
	    }
	}
	
	@PutMapping("/update/{pathwayId}")
	@Operation(summary = "Update a pathway's information", tags = { "Update Pathway" })
	public ResponseEntity<ApiResponse<String>> updatePathway(
	        @PathVariable Long pathwayId, @Valid @RequestBody PathwayDTO pathwayDTO) {
	    try {
	        boolean updated = pathwayService.updatePathway(pathwayDTO, pathwayId);
	        if (updated) {
	            return ResponseUtil.createSuccessResponse(HttpStatus.OK, "Pathway updated successfully", "updated");
	        } else {
	            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Pathway not found with ID: " + pathwayId, null);
	        }
	    } catch (EntityNotFoundException e) {
	        return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Pathway not found with ID: " + pathwayId, e.getMessage());
	    } catch (Exception e) {
	        return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update pathway", e.getMessage());
	    }
	}
	
	@DeleteMapping("/delete/{pathwayId}")
	@Operation(summary = "Delete a pathway by ID", tags = { "Delete Pathway By Id" })
	public ResponseEntity<ApiResponse<String>> deletePathway(@PathVariable Long pathwayId) {
	    try {
	        boolean deleted = pathwayService.deletePathway(pathwayId);
	        if (deleted) {
	            return ResponseUtil.createSuccessResponse(HttpStatus.OK, "Pathway deleted successfully", "deleted");
	        } else {
	            return ResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, "Pathway not found with ID: " + pathwayId, null);
	        }
	    } catch (Exception e) {
	        return ResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete pathway", e.getMessage());
	    }
	}
}