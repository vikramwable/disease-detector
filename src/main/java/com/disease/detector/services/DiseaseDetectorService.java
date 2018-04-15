package com.disease.detector.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
public class DiseaseDetectorService
{

  /**
   * Will detect disease and return probable disease name
   */
  public Map detectDisease(MultipartFile multipartFile) throws Exception
  {
    File file = convert(multipartFile);
    Map<String, List<String>> diseases = getAllDiseases();
    return findDisease(file, diseases);
  }


  private Map findDisease(File file, Map<String, List<String>> diseasesMap) throws IOException
  {
    Map<String, String> map = new HashMap<>();
    Set<String> diseases = diseasesMap.keySet();
    for (String disease : diseases)
    {
      List<String> files = diseasesMap.get(disease);
      if (isDiseaseAvailable(files, disease, file))
      {
        map.put("disease", StringUtils.capitalize(disease.replace("_", " ")));
        map.put("description", getDiseaseDescription(files, disease));
        return map;
      }
    }
    if (map.get("disease") == null)
    {
      map.put("disease", "Not able to find disease");
    }
    return map;
  }

  private boolean isDiseaseAvailable(List<String> files, String disease, File sourceFile)
  {
    for (String fileName : files)
    {
      if (!fileName.endsWith("txt"))
      {
        File file = getFile("Images/" + disease + "/" + fileName);
        if (ImageUtils.visuallyCompare(sourceFile, file))
        {
          return true;
        }
      }
    }
    return false;
  }

  private String getDiseaseDescription(List<String> files, String disease) throws IOException
  {
    String content = "";
    for (String fileName : files)
    {
      if (fileName.endsWith("txt"))
      {
        File file = getFile("Images/" + disease + "/" + fileName);
        content = FileUtils.readFileToString(file, "UTF-8");
      }
    }
    return content;
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
