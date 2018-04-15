package com.disease.detector.controller;

import com.disease.detector.services.DiseaseDetectorService;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController
{

  @Autowired
  private DiseaseDetectorService diseaseDetectorService;

  @GetMapping("/")
  public String index()
  {
    return "upload";
  }

  @PostMapping("/upload")
  public String singleFileUpload(@NotNull @RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes)
  {
    try
    {
      Map message = diseaseDetectorService.detectDisease(file);
      redirectAttributes.addFlashAttribute("disease", message.get("disease"));
      redirectAttributes.addFlashAttribute("description", message.get("description"));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "redirect:/uploadStatus";
  }

  @GetMapping("/uploadStatus")
  public String uploadStatus()
  {
    return "uploadStatus";
  }

}