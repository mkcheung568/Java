import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;
import java.text.*;
import java.time.ZonedDateTime;

public class G33m1CheungTestMain extends javax.swing.JFrame {
    /*
    Enter your code here
    */ 
    public G33m1CheungTestMain() {
            initComponents();
    }
    /*
    Enter your code here
    */                       
    private void initComponents() {
    
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 400));
    
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 400, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 300, Short.MAX_VALUE)
            );
    
            pack();
        }// </editor-fold>  //Igrone It (This basic window panel setting)                      
    
    
        public static void main(String args[]) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new G33m1CheungTestMain().setVisible(true);
                }
            });
     /*
    Enter your code here
    */ 
    G33m1AppSys as =new G33m1AppSys();
    as.submain();
    as.aboutme();
    String userno = JOptionPane.showInputDialog(null, "How many consultation do you want to make?");//let user enter the amount of user.
    int iuserno= Integer.parseInt(userno); //convert string to integer
    
    String fName = "G33m1Cheung.CSV";
    String[] in = new String[iuserno+1];//array initiate for input the amount of user record
    String [][] InfoArr = new String[iuserno][]; //2D array for JOption table
    in[0]="AR_ID,U_ID,Appointment_Time,Appointment_Type,Remarks";//assign array i[0] to title
    
    for(int i=0; i<iuserno; i++){//input the  infor to csv
    
        G33m1AppSys app = G33m1AppSys.makeap();//output to .CSV file
             
             
            
            in[i+1]=app.getdata();//array to storage each input data
              app.WriteTextFile(fName, in);//write data inside array to csv

              for (int b=0; b<iuserno; b++){
                InfoArr[b]  = app.getdata().split(",");
                System.out.print(InfoArr[b]);
        
            }
    
    }
    
           String[] reContent = G33m1AppSys.readTextFile(fName);// read file
                for(int a=0; a<reContent.length; a++)
                   System.out.println("Line"+ a +" "+reContent[a]);



                       String [] colName = {"Appointment ID","Student ID","Consultation Time","Consultation Type","Registration Time","Remarks"};
                          
                          
                       
     // show user record with a JTable, in JScrollPane, in JOptionPane
     JOptionPane.showMessageDialog(null, new JScrollPane(
         new JTable(new DefaultTableModel(InfoArr, colName))),
        "Information of ALL Appointment Record", JOptionPane.INFORMATION_MESSAGE);            

        }               
    }


























