package com.longhorn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class Webserver {
	private List<UniversityStudent> activeData = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(Webserver.class, args);
	}

	@GetMapping("/api/load/{caseId}")
	public Map<String, Object> loadCase(@PathVariable int caseId) {
		System.out.println("Loading Case " + caseId);
		if (caseId == 1) {
			activeData = Main.generateTestCase1();
		} else if (caseId == 2)
			activeData = Main.generateTestCase2();
		else if (caseId == 3)
			activeData = Main.generateTestCase2();
		else
			activeData = Main.generateTestCase1();

		Main.gradeLab(activeData, caseId);
		return convertToGraphDTO(activeData, "Loaded Test Case " + caseId);
	}

	@PostMapping("/api/roommates")
	public Map<String, Object> runRoommates() {
		activeData.forEach(s -> s.setRoommate(null));

		GaleShapley.assignRoommates(activeData);

		return convertToGraphDTO(activeData, "Roommates Assigned");
	}

	@GetMapping("/api/path")
	public List<String> findPath(@RequestParam String start, @RequestParam String targetCo) {
		UniversityStudent startStudent = activeData.stream()
				.filter(s -> s.getName().equals(start))
				.findFirst().orElse(null);

		if (startStudent == null)
			return Collections.emptyList();

		StudentGraph graph = new StudentGraph(activeData);
		ReferralPathFinder finder = new ReferralPathFinder(graph);
		List<UniversityStudent> path = finder.findReferralPath(startStudent, targetCo);

		if (path == null)
			return Collections.emptyList();
		return path.stream().map(UniversityStudent::getName).collect(Collectors.toList());
	}

	@GetMapping("/api/student/{name}")
	public Map<String, Object> getStudentDetails(@PathVariable String name) {
		UniversityStudent s = activeData.stream()
				.filter(st -> st.getName().equals(name))
				.findFirst().orElse(null);

		Map<String, Object> response = new HashMap<>();
		if (s != null) {
			response.put("name", s.getName());
			response.put("requests", s.getFriendRequests());
			response.put("chats", s.getChatHistory());
		}
		return response;
	}

	private Map<String, Object> convertToGraphDTO(List<UniversityStudent> students, String status) {
		Map<String, Object> response = new HashMap<>();
		response.put("status", status);

		List<Map<String, String>> nodes = students.stream().map(s -> {
			Map<String, String> m = new HashMap<>();
			m.put("id", s.getName());
			m.put("roommate", s.getRoommate() != null ? s.getRoommate().getName() : null);
			return m;
		}).collect(Collectors.toList());

		List<Map<String, Object>> links = new ArrayList<>();

		for (int i = 0; i < students.size(); i++) {
			for (int j = i + 1; j < students.size(); j++) {
				UniversityStudent s1 = students.get(i);
				UniversityStudent s2 = students.get(j);

				int weight = s1.calculateConnectionStrength(s2);

				if (weight > 0) {
					Map<String, Object> link = new HashMap<>();
					link.put("source", s1.getName());
					link.put("target", s2.getName());
					link.put("weight", weight);
					links.add(link);
				}
			}
		}

		response.put("nodes", nodes);
		response.put("links", links);
		return response;
	}
}
