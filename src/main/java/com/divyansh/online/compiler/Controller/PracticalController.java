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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.divyansh.online.compiler.Entity.PracticeEntity;
import com.divyansh.online.compiler.Repository.PracticeRepository;
import com.divyansh.online.compiler.Request.PracticalRequest;
import com.divyansh.online.compiler.Service.CompileRequest;

@CrossOrigin
@RestController
public class PracticalController {


	@Autowired
	private CompileRequest compileRequest;
	
	@Autowired
	private PracticeRepository practiceRepository;
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping("/practice/createQn")
	public ResponseEntity<?> createQn(@ModelAttribute PracticalRequest practical) throws IOException, InterruptedException{
		
		LocalDateTime ldt = LocalDateTime.now();
		String timestamp = "-" + new Date().getTime();
		
		String qFilename = practical.getEmail() + "_Question" + timestamp + ".txt";
		String tcFilename = practical.getEmail() + "_TestCase" + timestamp + ".txt";
		String tcAnsFilename = practical.getEmail() + "_TestCaseAns" + timestamp + ".txt";
		
		String qFilePath = "practice/Question/" + practical.getEmail() + "_Question" + timestamp + ".txt";
		String tcFilePath = "practice/TestCase/" + practical.getEmail() + "_TestCase" + timestamp + ".txt";
		String tcAnsFilePath = "practice/TestCase/" + practical.getEmail() + "_TestCaseAns" + timestamp + ".txt";
		
		
		try {
			compileRequest.savePracticeQuestion(practical.getQuestion(), qFilePath);
			compileRequest.savePracticeQuestionTestCase(practical.getTestcase(), tcFilePath);
			compileRequest.savePracticeQuestionTestCaseAnswer(practical.getTestcaseAns(), tcAnsFilePath);
			
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		practiceRepository.save(new PracticeEntity(practical.getEmail(), practical.getTitle(), practical.getType(),
				practical.getHints(), practical.getRelatedTopic(), qFilename, tcFilename, tcAnsFilename)); 
		
		return new ResponseEntity("Question created successfully", HttpStatus.OK);
	}
	
	@GetMapping("/practice/createQn")
	public ResponseEntity<?> getAllPracticeQn() throws Exception {
		return ResponseEntity.ok(practiceRepository.findAll());
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping("/practice/question/{qid}")
	public ResponseEntity<?> getQuestionByID(@PathVariable(value="qid") Long QID) throws Exception {
		return ResponseEntity.ok(practiceRepository.findQuestionByQID(QID));
	}
	
	@GetMapping("/practice/PracticeQn/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchPracticeQn(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "practice/Question/";
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
	

	@GetMapping("/practice/testCase/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchPracticeQnTestCase(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "practice/TestCase/";
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
	
	@GetMapping("/practice/practiceAns/{filename:.+}")
	@ResponseBody
	public ResponseEntity<InputStreamResource> fetchPracticeTestCaseAns(@PathVariable String filename) throws FileNotFoundException {
		String filePath = "practice/TestCase/";
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
