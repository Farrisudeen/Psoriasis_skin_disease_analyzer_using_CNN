package com.example.skindetect.controller;

import com.example.skindetect.dto.PredictionResult;
import com.example.skindetect.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    @Autowired
    private ModelService modelService;

    @GetMapping("/upload")
    public String uploadForm() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               Model model) throws Exception {
        if (file.isEmpty()) {
            return "index";
        }
        String uploadDir = "src/main/resources/static/file/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
        file.transferTo(filePath);

        PredictionResult result = modelService.predict(filePath);

        model.addAttribute("msg", result.getLabel());
        model.addAttribute("dos", result.getDos());
        model.addAttribute("donts", result.getDonts());
        model.addAttribute("normal", result.isNormal());
        model.addAttribute("filename", file.getOriginalFilename());
        return "prediction_result";
    }
}
