package com.sicnu.cs.servlet.basis;

import com.sicnu.cs.servlet.http.SessionManager;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

public class ServletTest {

    @Test
    public void test(){
        Date date=new Date();
        DateFormat format= new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z",
                Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println(format.format(date));

        SessionManager manager=new SessionManager(null);
        final ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<>();
        for (int i=0;i<200;i++){
            new Thread(() -> {
                String id=manager.createSession();
                queue.add(id);
                id=manager.createSession();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.add(id);
            }).start();
        }


        for (int i=0;i<100;i++){
            new Thread(() -> {
                String id=manager.createSession();


                queue.add(id);
            }).start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LongAdder gethit=new LongAdder();

        for (int i=400;i>0;i--){
            String id=queue.poll();
            Thread a=new Thread(() -> {
                HttpSession session=manager.getSession(id);
                if (session!=null){
                    gethit.add(1);
                }
            });

            Thread b=new Thread(() -> {
                System.out.println("change"+(manager.changeId(id)==null));

            });

            a.start();
            b.start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end"+gethit.longValue());
    }
}













