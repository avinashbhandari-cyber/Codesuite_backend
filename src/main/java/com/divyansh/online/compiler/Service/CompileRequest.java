package com.divyansh.online.compiler.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.divyansh.online.compiler.Entity.Response;
import com.divyansh.online.compiler.Entity.Result;

@Service
public class CompileRequest {
	
	@Autowired
	EntryPointRequest entryPointRequest;
	
	public ResponseEntity<Object> compile(MultipartFile codeFile, 
			MultipartFile inputFile, String language) throws IOException, InterruptedException {
		String folder = "";
		String file = "";
		
		if ("c".equalsIgnoreCase(language)) {
			folder = "program_c";
			file = "main.c";
			entryPointRequest.entrypointCFile(codeFile != null ? codeFile.getOriginalFilename() : "main.c", inputFile);
		}
		else if ("cpp".equalsIgnoreCase(language)) {
			folder = "program_cpp";
			file = "main.cpp";
			entryPointRequest.entrypointCppFile(codeFile != null ? codeFile.getOriginalFilename() : "main.cpp", inputFile);
		}
		else if ("java".equalsIgnoreCase(language)) {
			folder = "program_java";
			file = "main.java";
			entryPointRequest.entrypointJavaFile(codeFile != null ? codeFile.getOriginalFilename() : "main.java", inputFile);			
		}
		else if ("python".equalsIgnoreCase(language)) {
			folder = "program_python";
			file = "main.py";
			entryPointRequest.entrypointPythonFile(codeFile != null ? codeFile.getOriginalFilename() : "main.py", inputFile);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Response("Unsupported language: " + language, "Error during Program Compilation!", LocalDateTime.now()));
		}
		
		new File(folder).mkdirs();
		
		UploadFiles(codeFile, folder + "/" + file);
		
		File inputTarget = null;
		if (inputFile != null && !inputFile.isEmpty()) {
			String inFilename = inputFile.getOriginalFilename();
			if (inFilename == null || inFilename.trim().isEmpty()) {
				inFilename = "input.txt";
			}
			inputTarget = new File(folder, inFilename);
			UploadFiles(inputFile, folder + "/" + inFilename);
		}
		
		LocalDateTime ldt = LocalDateTime.now();
		String image = "container" + new Date().getTime();
		
		Result result;
		if (isDockerAvailable()) {
			result = execProgram(folder, image);
			try {
				new ProcessBuilder("docker", "rmi", image).start().waitFor(5, TimeUnit.SECONDS);
			} catch (Exception ignored) {}
		} else {
			result = execNative(folder, file, inputTarget, language);
		}
		
		String statuscode = result.getStatus();
		
		removeFiles(folder, file);
		if (inputTarget != null) {
			inputTarget.delete();
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(new Response(result.getOutput(), statuscode, ldt));
	}

	private boolean isDockerAvailable() {
		try {
			Process process = new ProcessBuilder("docker", "--version").start();
			return process.waitFor(3, TimeUnit.SECONDS) && process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private Result execNative(String folder, String file, File inputFile, String language) {
		StringBuilder outputBuilder = new StringBuilder();
		String statusResponse;
		int exitCode = -1;
		File sourceFile = new File(folder, file);
		
		try {
			if ("java".equalsIgnoreCase(language)) {
				ProcessBuilder pb = new ProcessBuilder("java", sourceFile.getAbsolutePath());
				pb.directory(new File(folder));
				if (inputFile != null && inputFile.exists()) {
					pb.redirectInput(inputFile);
				}
				Process process = pb.start();
				boolean finished = process.waitFor(10, TimeUnit.SECONDS);
				if (!finished) {
					process.destroyForcibly();
					return new Result("Time Limit Exceeded", "Execution timed out after 10 seconds.\n");
				}
				exitCode = process.exitValue();
				readProcessStreams(process, outputBuilder);
			} else if ("python".equalsIgnoreCase(language)) {
				if (isCommandAvailable("python")) {
					ProcessBuilder pb = new ProcessBuilder("python", sourceFile.getAbsolutePath());
					pb.directory(new File(folder));
					if (inputFile != null && inputFile.exists()) {
						pb.redirectInput(inputFile);
					}
					Process process = pb.start();
					boolean finished = process.waitFor(10, TimeUnit.SECONDS);
					if (!finished) {
						process.destroyForcibly();
						return new Result("Time Limit Exceeded", "Execution timed out after 10 seconds.\n");
					}
					exitCode = process.exitValue();
					readProcessStreams(process, outputBuilder);
				} else {
					return new Result("Error during Program Compilation!", 
							"Python is not installed on this system. Please install Python or Docker to run Python programs.\n");
				}
			} else if ("c".equalsIgnoreCase(language) || "cpp".equalsIgnoreCase(language)) {
				String compiler = "c".equalsIgnoreCase(language) ? "gcc" : "g++";
				if (isCommandAvailable(compiler)) {
					File exeFile = new File(folder, "main.exe");
					ProcessBuilder compilePb = new ProcessBuilder(compiler, sourceFile.getAbsolutePath(), "-o", exeFile.getAbsolutePath());
					Process compileProc = compilePb.start();
					boolean compiled = compileProc.waitFor(15, TimeUnit.SECONDS);
					if (!compiled || compileProc.exitValue() != 0) {
						readProcessStreams(compileProc, outputBuilder);
						return new Result("Error during Program Compilation!", outputBuilder.toString());
					}
					ProcessBuilder runPb = new ProcessBuilder(exeFile.getAbsolutePath());
					runPb.directory(new File(folder));
					if (inputFile != null && inputFile.exists()) {
						runPb.redirectInput(inputFile);
					}
					Process runProc = runPb.start();
					boolean finished = runProc.waitFor(10, TimeUnit.SECONDS);
					if (!finished) {
						runProc.destroyForcibly();
						return new Result("Time Limit Exceeded", "Execution timed out after 10 seconds.\n");
					}
					exitCode = runProc.exitValue();
					readProcessStreams(runProc, outputBuilder);
					exeFile.delete();
				} else {
					return new Result("Error during Program Compilation!", 
							compiler.toUpperCase() + " is not installed on this system. Please install MinGW GCC (" + compiler + ") or Docker to run C/C++ programs.\n");
				}
			} else {
				return new Result("Error during Program Compilation!", "Unsupported programming language: " + language);
			}
			
			if (exitCode == 0) {
				statusResponse = "Program Executed Successfully";
			} else {
				statusResponse = "Error during Program Compilation!";
			}
		} catch (Exception e) {
			statusResponse = "Execution Error";
			outputBuilder.append("Error executing code: ").append(e.getMessage());
		}
		
		return new Result(statusResponse, outputBuilder.toString());
	}

	private boolean isCommandAvailable(String command) {
		try {
			Process process = new ProcessBuilder(command, "--version").start();
			return process.waitFor(3, TimeUnit.SECONDS) && process.exitValue() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private void readProcessStreams(Process process, StringBuilder builder) throws IOException {
		try (BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		     BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
			String line;
			while ((line = processReader.readLine()) != null) {
				builder.append(line).append(System.lineSeparator());
			}
			while ((line = errorReader.readLine()) != null) {
				builder.append(line).append(System.lineSeparator());
			}
		}
	}

	private Result execProgram(String folder, String image) throws IOException, InterruptedException {
		int status;
		String[] docker = new String[] {"docker", "image", "build", folder, "-t", image};
		ProcessBuilder processbuild = new ProcessBuilder(docker);
		Process process = processbuild.start();
		status = process.waitFor();
		
		String[] dockerRun = new String[] { "docker", "run", "--rm", image };
		ProcessBuilder processrun = new ProcessBuilder(dockerRun);
		Process processr = processrun.start();
		status = processr.waitFor();
		
		BufferedReader processReader = new BufferedReader(new InputStreamReader(processr.getInputStream()));
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(processr.getErrorStream()));
		StringBuilder builder = new StringBuilder();
		
		String statusResponse;
		String line = null;

		while ((line = processReader.readLine()) != null) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		
		if (status == 0) {
			statusResponse = "Program Executed Successfully";
		}
		else {
			statusResponse = "Error during Program Compilation!";
			while ((line = errorReader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		}
		
		return new Result(statusResponse, builder.toString());
	}

	public void savePracticeQuestion(MultipartFile file, String filename) throws IOException {
		byte[] data = file.getBytes();
		Path loc = Paths.get(filename);
		if (loc.getParent() != null) {
			Files.createDirectories(loc.getParent());
		}
		Files.write(loc, data);
	}
	
	public void savePracticeQuestionTestCase(MultipartFile file, String filename) throws IOException {
		byte[] data = file.getBytes();
		Path loc = Paths.get(filename);
		if (loc.getParent() != null) {
			Files.createDirectories(loc.getParent());
		}
		Files.write(loc, data);
	}
	
	public void savePracticeQuestionTestCaseAnswer(MultipartFile file, String filename) throws IOException {
		byte[] data = file.getBytes();
		Path loc = Paths.get(filename);
		if (loc.getParent() != null) {
			Files.createDirectories(loc.getParent());
		}
		Files.write(loc, data);
	}
	
	private void UploadFiles(MultipartFile file, String filename) throws IOException {
		byte[] data = file.getBytes();
		Path loc = Paths.get(filename);
		if (loc.getParent() != null) {
			Files.createDirectories(loc.getParent());
		}
		Files.write(loc, data);
	}
	
	public void uploadAboutContest(MultipartFile file, String filename) throws IOException {
		byte[] data = file.getBytes();
		Path loc = Paths.get(filename);
		if (loc.getParent() != null) {
			Files.createDirectories(loc.getParent());
		}
		Files.write(loc, data);
	}
	
	private boolean removeFiles(String folder, String file) {
		if (folder != null && file != null) {
			String filename = folder + "/" + file;
			new File(filename).delete();
			return true;
		}
		return false;
	}

}
