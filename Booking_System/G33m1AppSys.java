import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;
import java.text.*;
import java.time.ZonedDateTime;

//Appointment system for physic 

public class G33m1AppSys extends javax.swing.JFrame {
    /*
    Enter your code here
    */ 
    String ARID, UID, RTIME, APTYPE,MST,REMARK;



    public G33m1AppSys(String arid, String uid, String rtime, String aptype, String mst, String remark){
       ARID=arid;
       UID=uid;
       RTIME=rtime;
       APTYPE=aptype;
       MST=mst;
       REMARK=remark;



    }



    public G33m1AppSys() {
            initComponents();
    }
    /*
    Enter your code here
    */    
    
    
    public void submain(){

        JOptionPane.showMessageDialog(null,"<html>This is  Physics consultation appointment.<br>Student may book a consultation with their Physics teacher.<br>Student may consultant about lesson content,exercises,other tutorial lesson or others </html>","Main",JOptionPane.INFORMATION_MESSAGE);

    }


    public void aboutme(){

        ImageIcon photo = new ImageIcon("Mike.jpg");

        JOptionPane.showMessageDialog(null,"<html>Author:<br>Cheung Kit Fung</html>", "About_Me",JOptionPane.INFORMATION_MESSAGE,photo);
    
    }

    public static G33m1AppSys makeap(){

        String aid,id,aptime,type,mt,apremark;
        Random rand = new Random();
        aid= "AR"+Integer.toString(rand.nextInt(1000000000));//random generate number
    id = JOptionPane.showInputDialog(null,"Please enter student ID:");
    String [] abc={ "Monday_16:00-18:00", "Wednesday_13:00-15:00", "Friday_10:00-12:00" };
    aptime =(String) JOptionPane.showInputDialog(null,"Please choose the time slope for consultation", "Consultation time Slot", JOptionPane.INFORMATION_MESSAGE, null,abc,abc[0]);
    String [] def={"Lesson Content","Exercise and Assignment","Extra Tutorial Lesson","Others"};
    type = (String) JOptionPane.showInputDialog(null,"Which aspect do you want make consultation?", "Consultation Type",JOptionPane.INFORMATION_MESSAGE,null, def, def[0]);
    apremark = JOptionPane.showInputDialog(null,"If you select 'Others' please input the detail here(Click OK if you have not)");
    mt=Long.toString(ZonedDateTime.now().toInstant().toEpochMilli());//time to millisecond
    

     G33m1AppSys rets = new G33m1AppSys(aid,id,aptime,type,mt,apremark);//create new object
     return rets;// return to construcor.


    }


    public String getdata(){
      
        return ARID+","+UID+","+RTIME+","+APTYPE+","+MST+","+REMARK;

    }

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
                    new G33m1AppSys().setVisible(true);
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
    in[0]="AR_ID,U_ID,Appointment_Time,Appointment_Type,Registration Time,Remarks";//assign array i[0] to title
    
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
                               "Information of All Appointment Record", JOptionPane.INFORMATION_MESSAGE);
    


        }   
        
        public static boolean WriteTextFile(String fileName, String[] fileContent) {
            try {
                PrintWriter outStream = new PrintWriter(fileName);
                for (int i = 0; i < fileContent.length; i++)
                    outStream.println(fileContent[i]); // write data into text file, in a line
                outStream.close(); //close the stream
                System.out.println(">>> Writing File [" + fileName + "] FINISHED" );
                } catch (FileNotFoundException fnfE){
                System.out.println(">>> Exception, FileNotFoundException");
                return false;
                }
                return true;
            }
                
                 public static String[] readTextFile(String fileName){
                ArrayList<String> retAList = new ArrayList<String>(); // arraylist
               try{
                String strLine;
                BufferedReader bufferReader = new BufferedReader(new FileReader(fileName));
                // read file to string until end
                while ((strLine = bufferReader.readLine()) != null) { // read a line data
               retAList.add(strLine); // add the read string line data to arraylist
                }
                bufferReader.close(); //close the stream
                System.out.println(">>> Reading File [" + fileName + "] FINISHED" );
                    } catch (FileNotFoundException fnfE){
                        System.out.println(">>> Exception, FileNotFoundException");
                        return null;
                    } catch (IOException ioE){
                        System.out.println(">>> Exception, IOException");
                        return null;
                    }
                        return retAList.toArray(new String[retAList.size()]);// ArrayList to String array
            }
         




    }