package com.Pdf.Splitter.Controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MyController {

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping("/split-pdf")
	public String splitPdf(@RequestParam("pdfFile") MultipartFile pdfFile, Model model) {

		if (pdfFile.isEmpty()) {
			return "index";
		}
		List<String> url = new ArrayList<>();
		try {
			String name = pdfFile.getOriginalFilename();
			System.out.println("name of pdf is " + pdfFile.getOriginalFilename());
			InputStream inputStream = pdfFile.getInputStream();
			PDDocument document = PDDocument.load(inputStream);
			Splitter splitter = new Splitter();
			List<PDDocument> split = splitter.split(document);

			int num = 1;
			for (PDDocument pdDocument : split) {
				String splitPdfPath = "E:\\pdfSplitter\\" + name.substring(0, name.length() - 4) + "(" + num++
						+ ").pdf";
				pdDocument.save(splitPdfPath);
				System.out.println("inside for loop");
				url.add(splitPdfPath);

			}
			System.out.println("outside loop");
			document.close();
			System.out.println("last one");
			model.addAttribute("urls", url);
			return "result";

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("message", "Error splitting PDF.");
			return "index";
		}

	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadPdf(@RequestParam("filename") String filename) throws IOException {
		File file = new File(filename);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", file.getName());
		headers.setContentLength(file.length());

		return new ResponseEntity<>(org.apache.commons.io.FileUtils.readFileToByteArray(file), headers,
				org.springframework.http.HttpStatus.OK);
	}

}
