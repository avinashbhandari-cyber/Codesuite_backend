package com.divyansh.online.compiler.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.divyansh.online.compiler.Entity.ContestEntity;
import com.divyansh.online.compiler.Entity.ContestQnEntity;
import com.divyansh.online.compiler.Entity.PracticeEntity;
import com.divyansh.online.compiler.Repository.ContestQnRepository;
import com.divyansh.online.compiler.Repository.ContestRepository;
import com.divyansh.online.compiler.Request.ContestQnRequest;
import com.divyansh.online.compiler.Request.ContestRequest;
import com.divyansh.online.compiler.Service.CompileRequest;

@CrossOrigin
@RestController
public class ContestController {
	
	@Autowired
	private CompileRequest compileRequest;
	
	@Autowired
	private ContestRepository contestRepository;

	@Autowired
	private ContestQnRepository contestQnRepository;
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping("/contest/createContest")
	public ResponseEntity<?> createContest(@ModelAttribute ContestRequest contest) throws IOException, InterruptedException{
		
		LocalDateTime ldt = LocalDateTime.now();
		String timestamp = "" + new Date().getTime();
		String filename = contest.getEmail() + "_About-" + timestamp + ".txt";
		String aboutContestPath = "contest/About/" + filename;
		
		try {
			compileRequest.uploadAboutContest(contest.getAbout(), aboutContestPath);
			
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} 
		
		contestRepository.save(new ContestEntity(contest.getEmail(), contest.getTitle(), contest.getStatus(),
				timestamp, filename)); 
		
		return new ResponseEntity("Contest Created Successfully", HttpStatus.OK);
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/contest/createContest")
	public ResponseEntity<?> getAllContest() throws Exception {
		return ResponseEntity.ok(contestRepository.findAll());
	}

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/contest/{ContestID}")
	public ResponseEntity<?> getContestByID(@PathVariable(value="ContestID") Long ContestID) throws Exception {
		return ResponseEntity.ok(contestRepository.findByContestID(ContestID));
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/contest/{ContestID}/questions")
	public ResponseEntity<?> getQuestionByContestID(@PathVariable(value="ContestID") Long ContestID) throws Exception {
		return ResponseEntity.ok(contestQnRepository.findQuestionByContestID(ContestID));
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/contest/{ContestID}/{QuestionID}")
	public ResponseEntity<?> getQuestionByQuestionID(@PathVariable(value="ContestID") Long ContestID, @PathVariable(value="QuestionID") Long QuestionID) throws Exception {
		return ResponseEntity.ok(contestQnRepository.findQuestionByQuestionID(ContestID, QuestionID));
	}	
	                                                           
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping("/contest/{ContestID}/createQn")
	public ResponseEntity<?> createContestQn(@PathVariable(value="ContestID") Long ContestID, @ModelAttribute ContestQnRequest contestQn) throws IOException, InterruptedException{
		
		LocalDateTime ldt = LocalDateTime.now();
		String timestamp = "-" + new Date().getTime();
		String qFilename = contestQn.getEmail() + "_Question" + timestamp + ".txt";
		String tcFilename = contestQn.getEmail() + "_TestCase" + timestamp + ".txt";
		String tcAnsFilename = contestQn.getEmail() + "_TestCaseAns" + timestamp + ".txt";
		
		String qFilePath = "contest/Question/" + contestQn.getEmail() + "_Question" + timestamp + ".txt";
		String tcFilePath = "contest/TestCase/" + contestQn.getEmail() + "_TestCase" + timestamp + ".txt";
		String tcAnsFilePath = "contest/TestCase/" + contestQn.getEmail() + "_TestCaseAns" + timestamp + ".txt";
		
		try {
			compileRequest.savePracticeQuestion(contestQn.getQuestion(), qFilePath);
			compileRequest.savePracticeQuestionTestCase(contestQn.getTestcase(), tcFilePath);
			compileRequest.savePracticeQuestionTestCaseAnswer(contestQn.getTestcaseAns(), tcAnsFilePath);
			
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		contestQnRepository.save(new ContestQnEntity(contestQn.getEmail(), ContestID, contestQn.getTitle(), contestQn.getType(),
				contestQn.getHints(), contestQn.getRelatedTopic(), contestQn.getNoOfTestCase(), qFilename, tcFilename, tcAnsFilename)); 
		
		return new ResponseEntity("Contest Created Successfully", HttpStatus.OK);
	}
	
	@GetMapping("/contest/AboutContest/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchContestAbout(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "contest/About/";
		File file = new File(filePath + filename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-diposition", "inline;filename=" + filename);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		
				return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
						.body(resource);
				
		}
	
	@GetMapping("/contest/ContestQn/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchContestQn(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "contest/Question/";
		File file = new File(filePath + filename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-diposition", "inline;filename=" + filename);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		
				return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
						.body(resource);
				
		}
	

	@GetMapping("/contest/testCase/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchContestTestCase(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "contest/TestCase/";
		File file = new File(filePath + filename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-diposition", "inline;filename=" + filename);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		
				return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
						.body(resource);
				
		}
	
	@GetMapping("/contest/tcAns/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchContestTestCaseAns(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "contest/TestCase/";
		File file = new File(filePath + filename);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("content-diposition", "inline;filename=" + filename);
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		
				return ResponseEntity.ok()
						.headers(headers)
						.contentType(MediaType.parseMediaType("text/plain; charset=utf-8"))
						.body(resource);
				
		}
	
}
