/*
    Copyright 2016 Rohit

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package Grep;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * GrepFiles
 * Responsibility: Find files recursively
 * Grep files in specified directory
 */

class GrepFiles {
    ImmutableList<String> packages;
    File directory;
    PackageStats pstats = PackageStats.getInstance();

    GrepFiles(String directoryPath,List<String> packageList) throws NullPointerException {
        Preconditions.checkNotNull(packageList);
        this.directory = new File(directoryPath);
        packages = ImmutableList.copyOf(packageList);
    }

    void printStatistics(){
        try {
            RecursiveFind recursiveFind = new RecursiveFind(directory, packages);
            Map<String,Integer> stats= PackageStats.getInstance().statistics;
            Iterator itr =stats.keySet().iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                System.out.println(key + "  "+stats.get(key));
            }
        }catch(FileNotFoundException ex){
            System.err.println("File Exceptions");
        }
    }
}

  /*
   Responsibility: Implements Algorithm to recursively descent and search
  */
class RecursiveFind{
    File directory;
    ImmutableList<String> packageList;
    PackageStats pstats = null;
    RecursiveFind(File directory,ImmutableList<String> packageList) throws FileNotFoundException{
        if(directory.isDirectory()){
            this.directory=directory;
            this.packageList=packageList;
        }
        pstats = PackageStats.getInstance();
        findFiles(directory);
    }

    //Recursively descend and search a file.
    void findFiles(File directory) throws FileNotFoundException {
        String[] fileList = directory.list();
        if(fileList!=null){
            //Iterate files
            for (String file : fileList) {
                File fileName = new File(directory.getAbsolutePath().concat("/").concat(file));
                if (!fileName.isDirectory()) {
                    Iterator itr = packageList.iterator();
                    //Iterate packages list
                    while (itr.hasNext()) {
                        findInFile(fileName, itr.next().toString());
                    }
                } else {//Its a directory
                    findFiles(fileName);
                }
            }
        }
    }

    void findInFile(File file, String packageName) throws FileNotFoundException{
        Scanner scanner = new Scanner(new FileInputStream(file));
        while(scanner.hasNextLine()){
            final String line = scanner.nextLine();
            if(line.contains(packageName)){
                pstats.incrementPackageCount(packageName);
                break;
            }
        }
    }
}

 /*
     Collect statistics Per package
 */
 class PackageStats{
    private static PackageStats instance = null;
    Map<String,Integer> statistics;
    private PackageStats(){
        statistics = new HashMap<String,Integer>();
    };
    static PackageStats getInstance(){
        if(instance==null) {
            instance = new PackageStats();
            return instance;
        }
        else
            return instance;
    }

    void incrementPackageCount(String packageName){
        int count=0;
        if(statistics.get(packageName)==null)
            statistics.put(packageName,++count);
        else {
            count=statistics.get(packageName);
            statistics.put(packageName,++count);
        }
    }
    Map<String,Integer> getReport(){
        return this.statistics;
    }
}

public class App {
    public static void main( String[] args ){
        //TODO Accept args to be used as command line utility
        /*String[] packages ={
                "com.google.common.annotations",
                "com.google.common.collect",
                "com.google.common.base",
                "com.google.common.cache",
                "com.google.common.escape",
                "com.google.common.eventbus",
                "com.google.common.graph",
                "com.google.common.hash",
                "com.google.common.html",
                "com.google.common.io",
                "com.google.common.math",
                "com.google.common.net",
                "com.google.common.primitives",
                "com.google.common.reflect",
                "com.google.common.util.concurrent",
                "com.google.common.xml"};
        List<String> list = new ArrayList();
        Collections.addAll(list,packages);
        GrepFiles grepper = new GrepFiles("/home/rohit/Downloads/sources/async-http-client",list);
        grepper.printStatistics();*/
    }
}
