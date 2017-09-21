/*
 * To change this license header, choose License Headers ^ Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TankWar;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
 


/**
 *
 * @author Konatan
 */
public class Menu extends JFrame implements ActionListener{
    Image img;
    ImageIcon icon1 = new ImageIcon("pic\\icon1.png");
    ImageIcon icon2 = new ImageIcon("pic\\icon2.png");
    ImageIcon icon3 = new ImageIcon("pic\\icon4.png");
    Toolkit kit=Toolkit.getDefaultToolkit();
    Dimension screenSize=kit.getScreenSize();
    int screenWidth=screenSize.width;
    int screenHeight=screenSize.height;
    JLabel label0;
    JPanel bt = new JPanel();
    JButton start = new JButton(icon1); 
    JButton HowToPlay = new JButton(icon2); 
    JButton exit = new JButton(icon3);
    Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
    TankWars tk;
    public Menu(){
        addWindowListener(new WindowAdapter() {  
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            }  
        }); 
        
        img=Toolkit.getDefaultToolkit().getImage("pic\\icon.png");
        this.setTitle("TankWar");
        this.setIconImage(img);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        
        this.setVisible(true);
        
        File file = new File("pic\\interface.jpg");
        BufferedImage bis = null;
        try{
            Image image = ImageIO.read(file);
            if (image== null)
                throw new IllegalArgumentException("Unable to read file");
            bis = new BufferedImage(screenWidth,screenHeight, BufferedImage.TYPE_INT_ARGB);
            bis.getGraphics().drawImage(image, 0, 0, screenWidth,screenHeight, null);
        }
        catch(Exception e) {
            System.err.println("Error loading texture: " + e);
        }  
        Image BG = bis;
        ImageIcon bg = new ImageIcon(BG);
        label0 = new JLabel(bg);
        
        
        Container con = this.getContentPane();
        
        label0.setBounds(0,0, bg.getIconWidth(),bg.getIconHeight());
        this.getLayeredPane().add(label0,new Integer(Integer.MIN_VALUE));
        con.setLayout(null);

        
        bt.setBounds(0,0,screenWidth,screenHeight);
        bt.setBackground(null);
        bt.setOpaque(false);
        bt.setLayout(null);
        start.setLocation((int) (screenWidth/1.63), (int) (screenHeight/2.57));
        start.setSize(585,90);
        start.setOpaque(false);  
        start.setContentAreaFilled(false);  
        start.setMargin(new Insets(0, 0, 0, 0));  
        start.setFocusPainted(false);  
        start.setBorderPainted(false);  
        start.setBorder(null);  
        con.add(start);
        start.setVisible(true);
        start.setCursor(cursor);
        
        HowToPlay.setLocation((int) (screenWidth/1.63), (int) (screenHeight/2));
        HowToPlay.setSize(585,90);
        HowToPlay.setOpaque(false);  
        HowToPlay.setContentAreaFilled(false);  
        HowToPlay.setMargin(new Insets(0, 0, 0, 0));  
        HowToPlay.setFocusPainted(false);  
        HowToPlay.setBorderPainted(false);  
        HowToPlay.setBorder(null);  
        con.add(HowToPlay);
        HowToPlay.setVisible(true);
        HowToPlay.setCursor(cursor);        
         
        exit.setLocation((int) (screenWidth/1.63), (int) (screenHeight/1.64));
        exit.setSize(585,90);
        exit.setOpaque(false);  
        exit.setContentAreaFilled(false);  
        exit.setMargin(new Insets(0, 0, 0, 0));  
        exit.setFocusPainted(false);  
        exit.setBorderPainted(false);  
        exit.setBorder(null);  
        con.add(exit);
        exit.setVisible(true);
        exit.setCursor(cursor);
        
        bt.add(start);
        bt.add(HowToPlay);
        bt.add(exit);
        this.add(bt);
        ((JPanel)con).setOpaque(false);
        this.validate(); 
        
        start.addActionListener(this);
        HowToPlay.addActionListener(this);
        exit.addActionListener(this);
        
        this.repaint();
         
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==start){
            bt.setVisible(false);
            this.add(new TankWars(this));
        }
        if(e.getSource()==HowToPlay){
            try {
                Desktop.getDesktop().open(new File("introduction.txt"));
            } catch (IOException ex) {
                System.out.println("Error");
            }
        }
        if(e.getSource()==exit){
            System.exit(0);
        }
    }
    public static void main(String[] args){
        new Menu();
    }
}
