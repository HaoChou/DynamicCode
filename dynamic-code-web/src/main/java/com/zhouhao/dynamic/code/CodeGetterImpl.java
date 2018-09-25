package com.zhouhao.dynamic.code;

import com.zhouhao.dynamic.code.core.CodeGetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhouhao.dynamic.code.core.zookeeper.ZkConstant.PATH_SPLIT_CHAR;

//Created by zhou on 2017/10/26
public class CodeGetterImpl implements CodeGetter {

    private Map<String,String> className2Code = new HashMap();

    private void refreshFileCode(){
        Map newClassName2Code=new HashMap();
        File directory=new File("/Users/zhou/Documents/zhouhaoWorkSpace/dynamiccode/dynamic-code-web/src/main/java/com/zhouhao/dynamic/code/groovy");
        File[] files=directory.listFiles();
        for(File file:files){
            newClassName2Code.put(getFileNameByPath(file.getPath()),getFileContent(file));
        }
        className2Code=newClassName2Code;
    }
    @Override
    public String getCodeByClassName(String className) {
        refreshFileCode();
        return className2Code.get(className);
    }

    @Override
    public List<String> getAllCode() {
        refreshFileCode();
        List<String> result = new ArrayList<>();
        for(Map.Entry entry: className2Code.entrySet()){
            result.add((String) entry.getValue());
        }
        return result;
    }


    public static void main(String[] args) {
        File directory=new File("/Users/zhou/Documents/zhouhaoWorkSpace/dynamiccode/dynamic-code-web/src/main/java/com/zhouhao/dynamic/code/groovy");

        File[] files=directory.listFiles();
        for(File file:files){
            System.out.println(getFileNameByPath(file.getPath()));
            System.out.println(getFileContent(file));
        }
    }

    private static String getFileNameByPath(String path){
        if(!path.contains(PATH_SPLIT_CHAR))
            return null;
        String result=path;
        while (result.contains(PATH_SPLIT_CHAR)) {
            result=result.substring(result.indexOf(PATH_SPLIT_CHAR)+1);
        }
        return result.substring(0,result.indexOf("."));
    }

    public static String getFileContent(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
}
