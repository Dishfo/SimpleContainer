package com.cs.sicnu.core;

import java.util.Objects;

public class ByteAcessTest {


    /**
     * 忽略大小写的字符串比较
     * @param s1
     * @param s2
     * @return
     */
    boolean isEqual(String s1,String s2){
        Objects.requireNonNull(s1);
        Objects.requireNonNull(s2);

        if (s1.length()!=s2.length()){
            return false;
        }

        for (int i=0;i<s1.length();i++){
            char c1=s1.charAt(i);
            char c2=s2.charAt(i);

            if (c1!=c2&&(Math.abs(c1-c2))!=32){
                return false;
            }
        }

        return true;
    }
}

   /*private void parseBytes(byte[] data,String boundary){
        String tmp="\r\n";
        System.out.println("boundary is "+boundary);
        String statrBoundary="--"+boundary;
        String endBoundary="--"+boundary+"--";
        ByteAcess acess=new HeapAcesss();
        try {
            acess.append(data,0,data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }



        int cur=0;
        int part=0;
        int linend=0;
        part=acess.find(statrBoundary.getBytes());
        int size=1024*1024*4;
        System.out.println(size);
        //part=acess.find(statrBoundary.getBytes());
        System.out.println(acess.find(statrBoundary.getBytes(),400));
        while (part>=0){

            while (linend>=0){
                part=acess.find(tmp.getBytes(),part)+2;
                linend=acess.find(tmp.getBytes(),part);
                String line=acess.getString(part,linend);
                System.out.println(line+"  len "+line.length());
                if (line.equals("")){
                    System.out.println("begin parse data");
                    linend+=2;
                    part=acess.find(statrBoundary.getBytes(),
                            linend);
                    byte[] bytes=acess.getRangeBytes(linend,part);
                    parePart(bytes);
                    break;
                }

            }
            break;
        }


    }

    private void parePart(byte[] bytes){
        System.out.println("file size is "+bytes.length);
        File file=new File("/home/dishfo/logs","test"+System.currentTimeMillis()+".png");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
