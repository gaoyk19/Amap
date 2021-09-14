package gaode;

import java.io.*;
import java.nio.Buffer;

public class HandleSpeedData {
    public static void main(String[] args) throws IOException {
//        String fileName="/home/gaoyk/Desktop/section0914_10.csv";
        File file=new File("/home/gaoyk/Desktop/section0914_10.csv");
        File outFile=new File("/home/gaoyk/Desktop/result.csv");
        if(!outFile.exists()){
            outFile.createNewFile();
        }

        LineNumberReader lineNumberReader=new LineNumberReader(new FileReader(file));
        BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(outFile));

//        lineNumberReader.skip(Long.MAX_VALUE);
//        int lines=lineNumberReader.getLineNumber();

        String line="";
        boolean mark =true;
        int preNumber=0;
        while((line=lineNumberReader.readLine())!=null){
            String[] lineNum=line.split(",");
            if(!mark){
                bufferedWriter.write(String.valueOf(preNumber)+","+lineNum[3]);
                bufferedWriter.newLine();

                int tempNumber=preNumber+Integer.parseInt(lineNum[2]);
                preNumber=tempNumber;
                String time=String.valueOf(tempNumber);
                String writerLine=time+","+lineNum[3];
                System.out.println(time+","+lineNum[3]);

                bufferedWriter.write(writerLine);
                bufferedWriter.newLine();
            }else{
                String writerLine=lineNum[2]+","+lineNum[3];
                bufferedWriter.write(writerLine);
                bufferedWriter.newLine();
                mark=false;
            }
        }

        lineNumberReader.close();
        bufferedWriter.close();
    }
}
