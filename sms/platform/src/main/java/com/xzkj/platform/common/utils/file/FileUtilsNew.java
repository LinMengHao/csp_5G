package com.xzkj.platform.common.utils.file;


import com.xzkj.platform.common.utils.NumberUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * 文件上传工具包
 */
public class FileUtilsNew {

    /**
     *
     * @param file 文件
     * @param path 文件存放路径
     * @return
     */
    public static boolean upload(MultipartFile file, String path){
    

        File dest = new File(path);

        //判断文件父目录是否存在
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }

        try {
            //保存文件
            file.transferTo(dest);
            return true;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }
    
    public static boolean copyFile(File oldFile,File newFile) {
    	 FileInputStream fis;
    	 FileOutputStream fos;
    	 createFile(newFile);
		try {
			fis = new FileInputStream(oldFile);
			fos = new FileOutputStream(newFile);
			byte[] read = new byte[1024];
			int len = 0;
			while((len = fis.read(read))!= -1){
				fos.write(read,0,len);
			}
			fis.close();
			fos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
    public static void createFile(File file) {
    	if(!file.getParentFile().exists()) {
    		file.getParentFile().mkdirs();
    	}
    	if(!file.exists()) {
    		try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    public static String txt2String(File file){
    	 BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try{
            br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s+System.lineSeparator());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result.toString();
    }
    
    public static void deleteDir(String dirPath){
		File file = new File(dirPath);
		if(file.isFile())
		{
			file.delete();
		}else
		{
			File[] files = file.listFiles();
			if(files == null)
			{
				file.delete();
			}else
			{
				for (int i = 0; i < files.length; i++) 
				{
					deleteDir(files[i].getAbsolutePath());
				}
				file.delete();
			}
		}
	}
    
    
    /**
	 * 获取文件最大行数
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static int getTotalLines(File file) {
		if (!file.exists()) {
			return -1;
		}
		LineNumberReader reader = null;
		FileReader in = null;
		int lines = 0;
		try {
			in = new FileReader(file);
			reader = new LineNumberReader(in);
			String s = reader.readLine();
			while (s != null) {
				if(s.equals("\n")||s.equals("\r")||s.equals("\r\n")||s.equals("\n\r")||s.equals("")){
					return lines;
				}
				lines++;
				s = reader.readLine();
			}
		} catch (IOException e) {
			return -1;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
		return lines;
	}
	
	/**
	 * 读取文件的指定行内容
	 * 
	 * @param lineNumber
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static String readAppointedLineNumber(File file, int lineNumber) {
		if (lineNumber < 0 || lineNumber > getTotalLines(file)) {
			return null;
		}
		FileReader in;
		try {
			in = new FileReader(file);
		} catch (FileNotFoundException e) {
			return null;
		}
		LineNumberReader reader = new LineNumberReader(in);
		String s;
		reader.setLineNumber(lineNumber);
		try {
			s = reader.readLine().trim();
		} catch (IOException e) {
			return null;
		}
		try {
			reader.close();
		} catch (IOException e1) {
			return null;
		}
		try {
			in.close();
		} catch (IOException e) {
			return null;
		}
		return s;
	}

	
	/**
     * 获取某个文件夹下的所有文件夹的文件
     *
     * @param fileNameList 存放文件名称的list
     * @param path 文件夹的路径
     * @return
     */
    public static void getAllFileName(String path,List<String> fileNameList) {
        File file = new File(path);
        List<File> tempList = FileUtilsNew.sortFileName(file);

        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).isFile()) {
                fileNameList.add(tempList.get(i).getName());
            }
            if (tempList.get(i).isDirectory()) {
                getAllFileName(tempList.get(i).getAbsolutePath(),fileNameList);
            }
        }
    }
    
    public static List<File> sortFileName(File file) {
    	File[] listFiles = file.listFiles();
		List<File> fileList = null;
		try{
			fileList = new ArrayList<>(Arrays.asList(listFiles));
			if(fileList == null){
				return null;
			}
		}catch (Exception e){
    		//e.printStackTrace();
    		return null;
		}

    	Collections.sort(fileList, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				String name1 = o1.getName();
				String name2 = o2.getName();
				return NumberUtils.getInteger(name1.substring(name1.length()-1), 0) - NumberUtils.getInteger(name2.substring(name2.length()-1), 0);
			}
    		
    	});
    	return fileList;
    }

	public static byte[] File2byte(File tradeFile){
		byte[] buffer = null;
		try
		{
			FileInputStream fis = new FileInputStream(tradeFile);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1)
			{
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		return buffer;
	}

    public static void main(String[] args) {
		List<String> list =new ArrayList<>();
		FileUtilsNew.getAllFileName("D:\\data\\sendFile\\202003261347359604781", list);
		list.remove("smil1.smil");
		for(String s:list) {
			System.out.println(s);
		}
	}
}