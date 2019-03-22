package com.hy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassPatchUtil {
	
	public static String fileName="配送票监控2";//补丁文件名
	
	public static String patchFile="C:/办公文档/增量包/"+fileName+".txt";//补丁文件路径
	public static String projectPath="C:/Development/WorkSpace_Online";//工作空间路径
	public static String webContent="/WebContent";//web应用文件夹名
	public static String projectNmae=null;//项目名称（下边会自动从补丁文件中获取）
	public static String classPath=null;//class存放路径(编译后的class，默认从/target/classes中取)
	public static String desPath="C:/办公文档/增量包";//增量包存放路径
	static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	public static String version=fileName+"_"+sdfDate.format(new Date());//补丁版本(补丁文件名_当前日期)
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		copyFiles(getPatchFileList());
	}
	
	@SuppressWarnings("resource")
	public static List<String> getPatchFileList() throws Exception{
		List<String> fileList=new ArrayList<String>();
		FileInputStream f = new FileInputStream(patchFile); 
		BufferedReader dr = new BufferedReader(new InputStreamReader(f,"utf-8"));
		String line;
		while((line=dr.readLine())!=null){ 
			if(line.indexOf("path")!=-1){
				line=line.replaceAll("\\s*", ""); 
				line=line.substring(line.indexOf("path")+4,line.length());
				if(classPath == null) {
					int index = line.indexOf("/src");
					index = index == -1 ? line.indexOf(webContent) : index;
					projectNmae = line.substring(0, index);
					projectPath += projectNmae;
					classPath = projectPath + "/target/classes";
				}
				line=line.replaceAll(projectNmae,"");
				fileList.add(line);
			}
		}
		return fileList;
	}
	
	public static void copyFiles(List<String> list){
		for(String fullFileName:list){
			if(fullFileName.indexOf("src/")!=-1 || fullFileName.indexOf("config/")!=-1){//对源文件目录下的文件处理
				String fileName=fullFileName.replace("/src","");
				fullFileName=classPath+fileName;
				if(fileName.endsWith(".java")){
					fileName=fileName.replace(".java",".class");
					fullFileName=fullFileName.replace(".java",".class");
				}
				String desFileNameStr=desPath+"/"+version+projectNmae+"/WEB-INF/classes"+fileName;
				String desFilePathStr=desFileNameStr.substring(0,desFileNameStr.lastIndexOf("/"));
				File desFilePath=new File(desFilePathStr);
				if(!desFilePath.exists()){
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, desFileNameStr);
				System.out.println(desFileNameStr);
			}else{//对普通目录的处理
				String desFileName=fullFileName.replaceAll(webContent,"");
				fullFileName=projectPath+fullFileName;//将要复制的文件全路径
				String fullDesFileNameStr=desPath+"/"+version+projectNmae+desFileName;
				String desFilePathStr=fullDesFileNameStr.substring(0,fullDesFileNameStr.lastIndexOf("/"));
				File desFilePath=new File(desFilePathStr);
				if(!desFilePath.exists()){
					desFilePath.mkdirs();
				}
				copyFile(fullFileName, fullDesFileNameStr);
				System.out.println(fullDesFileNameStr);
			}
		}
		System.out.println("共 "+list.size()+"个文件复制完成");
	}

	private static void copyFile(String sourceFileNameStr, String desFileNameStr) {
		File srcFile=new File(sourceFileNameStr);
		File desFile=new File(desFileNameStr);
		try {
			copyFile(srcFile, desFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }
}
