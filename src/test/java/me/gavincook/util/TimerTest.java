package me.gavincook.util;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer用于定时调度任务。可以指定延后多久开始执行也可以指定具体时间点开始执行。有以下几个不足：
 * 1. 前一个任务的执行时间过长，或影响后一个任务的调度
 * 2. 如果执行任务时抛出异常，那么所有的任务都无法执行
 * 可以考虑使用{@link java.util.concurrent.ScheduledThreadPoolExecutor}
 * @author GavinCook
 * @date 2016-05-10
 * @since 1.0.0
 */
public class TimerTest {

    /**
     * 从现在开始每隔两秒重复执行一次任务
     * @throws InterruptedException
     */
    @Test
    public void testRepeatFromNow() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(getSimpleTimerTask("repeat from now on message"), 0, 2000);
        Thread.sleep(10000);
    }

    /**
     * 从2s后，每隔两秒重复执行一次任务
     * @throws InterruptedException
     */
    @Test
    public void testRepeatFromSpecialDate() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(getSimpleTimerTask("repeat from special date message"),
                new Date(System.currentTimeMillis()+2000), 2000);
        Thread.sleep(10000);
    }

    /**
     * 执行多个任务，这个每个任务都耗时2s。即使我们设置每个任务都间隔1s执行一次，这里也会每隔2s才会执行一个任务。也即是说前一个任务的时长会影响后一个任务的执行。
     * @throws InterruptedException
     */
    @Test
    public void testScheduleMultiTasks() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(getTimeConsumingTimerTask("repeat from now on message1"), 0, 1000);
        timer.schedule(getTimeConsumingTimerTask("repeat from now on message2"), 0, 1000);
        timer.schedule(getTimeConsumingTimerTask("repeat from now on message3"), 0, 1000);
        Thread.sleep(10000);
    }

    /**
     * 从当前时间的前4s开始调度任务，scheduleAtFixedRate方法会从设置的日期进行计算，比如这里从当前时间的前4s开始，每隔2s执行一次，那么timer开始
     * 就会执行三次该任务
     * @throws InterruptedException
     */
    @Test
    public void testFixedRateRepeatFromPreviouslyDate() throws InterruptedException {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(getSimpleTimerTask("repeat from special date message"),
                new Date(System.currentTimeMillis() - 4000), 2000);
        Thread.sleep(10000);
    }

    /**
     * 从当前时间的前4s开始调度任务，schedule方法中，如果设置的日期小于当前日期，则直接从当前日期计算。比如这里从当前时间的前4s开始，每隔2s执行一次，
     * 那么timer开始只会执行一次任务
     * @throws InterruptedException
     */
    @Test
    public void testRepeatFromPreviouslyDate() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(getSimpleTimerTask("repeat from special date message"),
                new Date(System.currentTimeMillis() - 4000), 2000);
        Thread.sleep(10000);
    }

    /**
     * get simple timer task instance: combine message and current time, then print to console
     * @param message
     * @return
     */
    private TimerTask getSimpleTimerTask(String message){
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println(message + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
    }

    private TimerTask getTimeConsumingTimerTask(String message){
        return new TimerTask() {
            @Override
            public void run() {
                System.out.println(message + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
