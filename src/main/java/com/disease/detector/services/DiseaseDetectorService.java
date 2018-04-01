package com.disease.detector.services;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
public class DiseaseDetectorService
{

  /**
   * Will detect disease and return probable disease name
   */
  public String detectDisease(MultipartFile multipartFile) throws Exception
  {
    File file = convert(multipartFile);
    Map<String, List<String>> diseases = getAllDiseases();
    return findDisease(file, diseases);
  }

  private String findDisease(File file, Map<String, List<String>> diseasesMap)
  {
    Set<String> diseases = diseasesMap.keySet();
    for (String disease : diseases)
    {
      List<String> files = diseasesMap.get(disease);
      if (isDiseaseAvailable(files, disease, file))
      {
        return StringUtils.capitalize(disease.replace("_", " "));
      }
    }
    return "Not able to find disease";
  }

  private boolean isDiseaseAvailable(List<String> files, String disease, File sourceFile)
  {
    for (String fileName : files)
    {
      File file = getFile("Images/" + disease + "/" + fileName);
      if (ImageUtils.visuallyCompare(sourceFile, file))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * get all folders from resource folder
   *
   * @return list of folders
   */
  private Map<String, List<String>> getAllDiseases()
  {
    Map<String, List<String>> diseaseSample = new HashMap<>();
    List<String> allDiseases = new ArrayList<>();
    URL path = getClass().getResource("/Images");
    File folder = new File(path.getPath());
    if (folder.exists() && folder.isDirectory())
    {
      for (File file : folder.listFiles())
      {
        if (file.isDirectory())
        {
          for (File f : file.listFiles())
          {
            allDiseases.add(f.getName());
          }
          diseaseSample.put(file.getName(), allDiseases);
          allDiseases = new ArrayList<>();
        }
      }
    }
    return diseaseSample;
  }

  private File getFile(String fileName)
  {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(classLoader.getResource(fileName).getFile());
  }

  public File convert(MultipartFile file) throws Exception
  {
    File convertedFile = new File(file.getOriginalFilename());
    convertedFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(convertedFile);
    fos.write(file.getBytes());
    fos.close();
    return convertedFile;
  }
}
