package gr.agroknow.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils
{

private List<String> fileList;
private   String OUTPUT_ZIP_FILE; //static final 
private   String SOURCE_FOLDER;//static final // "D:\\Zips"; SourceFolder path

public ZipUtils(String filename , String SrcFolderPath)
{
   fileList = new ArrayList<String>();
   setOutputFile(filename);
   setSrcFolder(SrcFolderPath);
   
   //generateFileList(new File(getSrcFolder()));
   //zipIt(getOutputFile());
  
}


/*public static void main(String[] args)
{
   ZipUtils appZip = new  ZipUtils(String filename , String SrcFolderPath);//ZipUtils();
   appZip.generateFileList(new File(SOURCE_FOLDER));
   appZip.zipIt(OUTPUT_ZIP_FILE);
}*/


public void setOutputFile(String OUTPUT_ZIP_FILE){
	this.OUTPUT_ZIP_FILE = OUTPUT_ZIP_FILE+".zip";;
		
}

public String getOutputFile(){
	 return this.OUTPUT_ZIP_FILE;
		
}


public void setSrcFolder(String SOURCE_FOLDER){
	this.SOURCE_FOLDER = SOURCE_FOLDER;
		
}

public String getSrcFolder(){
	 return this.SOURCE_FOLDER;
		
}




public void zipIt(String zipFile)
{
   byte[] buffer = new byte[1024];
   String source = "";
   FileOutputStream fos = null;
   ZipOutputStream zos = null;
   try
   {
      try
      {
         source = SOURCE_FOLDER.substring(SOURCE_FOLDER.lastIndexOf("\\") + 1, SOURCE_FOLDER.length());
      }
     catch (Exception e)
     {
        source = SOURCE_FOLDER;
     }
     fos = new FileOutputStream(zipFile);
     zos = new ZipOutputStream(fos);

     System.out.println("Output to Zip : " + zipFile);
     FileInputStream in = null;

     for (String file : this.fileList)
     {
        System.out.println("File Added : " + file);
        ZipEntry ze = new ZipEntry(source + File.separator + file);
        zos.putNextEntry(ze);
        try
        {
           in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
           int len;
           while ((len = in.read(buffer)) > 0)
           {
              zos.write(buffer, 0, len);
           }
        }
        finally
        {
           in.close();
        }
     }

     zos.closeEntry();
     System.out.println("Folder successfully compressed");

  }
  catch (IOException ex)
  {
     ex.printStackTrace();
  }
  finally
  {
     try
     {
        zos.close();
     }
     catch (IOException e)
     {
        e.printStackTrace();
     }
  }
}

public void generateFileList(File node)
{

  // add file only
  if (node.isFile())
  {
     fileList.add(generateZipEntry(node.toString()));

  }

  if (node.isDirectory())
  {
     String[] subNote = node.list();
     for (String filename : subNote)
     {
        generateFileList(new File(node, filename));
     }
  }
}

private String generateZipEntry(String file)
{
   return file.substring(SOURCE_FOLDER.length() + 1, file.length());
}
}    