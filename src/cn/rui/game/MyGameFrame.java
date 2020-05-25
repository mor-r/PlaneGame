package cn.rui.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

/**
 * 飞机小游戏的主窗口
 */
public class MyGameFrame extends Frame {

    Image planeImg=GameUtil.getImage("images/plane.png");
    Image bg=GameUtil.getImage("images/bg.png");

    Plane plane=new Plane(planeImg,400,400,30,30);
    //Shell shell=new Shell();
    Shell[] shells=new Shell[50];

    Explode explode;

    Date startTime=new Date();
    Date endTime;
    int period;//游戏持续的时间

    @Override
    public void paint(Graphics g) {//自动被调用，g相当于一支画笔
        Color c=g.getColor();
        Font f=g.getFont();

        g.drawImage(bg,0,0,500,500,null);

        plane.drawSelf(g);//画飞机

        //shell.draw(g);//画炮弹
        for(int i=0;i<shells.length;i++){//画出所有炮弹
            shells[i].draw(g);

            //飞机和炮弹的碰撞检测
            boolean collision=shells[i].getRect().intersects(plane.getRect());
            if(collision){
                plane.live=false;
                if(explode==null){
                    explode=new Explode(plane.x,plane.y);

                    endTime=new Date();
                    period=(int)((endTime.getTime()-startTime.getTime())/1000);
                }
                explode.draw(g);
            }

            if (!plane.live) {
                g.setColor(Color.WHITE);
                //g.setFont(new Font("宋体",Font.BOLD,20));
                g.drawString("您的游戏总时长为："+period+"秒",Constant.GAME_WIDTH/3,Constant.GAME_HEIGHT/2);
            }
        }
        g.setFont(f);
        g.setColor(c);
    }

    //帮助我们反复的重画窗口
    class PaintThread extends Thread{
        @Override
        public void run() {
            while(true){
                repaint();//重画
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //定义键盘监听的内部类
    class KeyMonitor extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            plane.addDirection(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            plane.minusDirection(e);
        }
    }

    /**
     * 初始化窗口
     */
    public void launchFrame(){
        this.setTitle("飞机小游戏");//设置窗口标题
        this.setVisible(true);//设置窗口可见
        this.setSize(Constant.GAME_WIDTH,Constant.GAME_HEIGHT);//设置窗口大小
        this.setLocation(500,300);//设置窗口位置

        //实现点击右上角关闭可实现真正意义上的关闭
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        new PaintThread().start();//启动重画窗口的线程
        addKeyListener(new KeyMonitor());//给窗口增加键盘的监听

        //初始化50个炮弹
        for(int i=0;i<shells.length;i++){
            shells[i]=new Shell();
        }
    }

    public static void main(String[] args) {
        MyGameFrame f=new MyGameFrame();
        f.launchFrame();
    }

    private Image offScreenImage=null;
    //双缓冲技术解决闪烁问题
    public void update(Graphics g){
        if(offScreenImage==null) {
            offScreenImage = this.createImage(Constant.GAME_WIDTH, Constant.GAME_HEIGHT);//这是游戏窗口的宽度和高度
        }

            Graphics gOff=offScreenImage.getGraphics();
            paint(gOff);
            g.drawImage(offScreenImage,0,0,null);
    }
}
