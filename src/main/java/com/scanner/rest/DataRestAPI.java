package com.scanner.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanner.model.Repo;
import com.scanner.model.SearchBody;
import com.scanner.util.Util;

/**
 * 
 * The REST API layer, injected the service, providing the connections for the frontend
 * 
 */
@RestController
@RequestMapping("github_scanner/api")
public class DataRestAPI {
	
	private static Set<Repo> repoSet = new HashSet<>();
	
	@PostMapping("add")
	public ResponseEntity<HttpStatus> add(@RequestBody List<String> list) {
		for (String s : list) {
			String path = Util.convert(s);
			Repo repo = Util.buildRepo(path);
			repoSet.add(repo);
			System.out.println(repo);
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}
	
	@PostMapping("search")
	public List<Repo> search(@RequestBody SearchBody searchBody) {
		List<Repo> res = new ArrayList<>();
		for (Repo repo : repoSet) {
			if ((searchBody.getName() == null || repo.getName().toLowerCase().contains(searchBody.getName().toLowerCase())) &&
				(searchBody.getType() == null || repo.getType().toLowerCase().contains(searchBody.getType().toLowerCase())) &&
				(searchBody.getIndustry() == null || repo.getIndustry().toLowerCase().contains(searchBody.getIndustry().toLowerCase())) &&
				(searchBody.getTechnology() == null || repo.getTechnology().toLowerCase().contains(searchBody.getTechnology().toLowerCase()))) {
				res.add(repo);
			}
		}
		return res;
	}
	
	@GetMapping("all")
	public Set<Repo> pollAccess() {
		return repoSet;
	}
	
	@DeleteMapping("reset")
	public ResponseEntity<HttpStatus> reset() {
		repoSet.clear();
		return ResponseEntity.ok(HttpStatus.OK);
	}
}










