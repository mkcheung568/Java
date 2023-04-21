

import javax.swing.table.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.security.*;

public class G33Create extends javax.swing.JFrame {

     String Uid,Uname,Urole,Yrofb,Remark;
     String Cryptedpw; //encrypted password for authentication
    
    

    public G33Create(String uid, String pw, String uname, String urole, String yrofb, String remark){ //initia the variable
      
        Uid=uid;
        Uname=uname;
        Cryptedpw=cryptPwdMD5(pw);
        Urole=urole;
        Yrofb=yrofb;
        Remark=remark;
        



    }

    public String getinfo()//get information with "," 
{
    return Uid+","+Cryptedpw+","+Uname+","+Urole+","+Yrofb+","+Remark;
      
	
}

    public static G33Create usercreates(){ //for admin create user
			
        String id, orgpwd,name,role, year, remark;
        
        id = JOptionPane.showInputDialog(null,"Please enter user ID:");
        orgpwd = JOptionPane.showInputDialog(null,"Please enter user password:");
        name = JOptionPane.showInputDialog(null,"Please enter user name:");
        role = JOptionPane.showInputDialog(null,"Please enter user role:");
        year = JOptionPane.showInputDialog(null,"Please enter user Year of birth:");
        remark = JOptionPane.showInputDialog(null,"Please enter user remark:");
        
        
        G33Create rets = new G33Create(id,orgpwd,name,role,year,remark); // create new  objct
        return rets; // return to construcor.


}

    public static String cryptPwdMD5(String pw){ //class method for password encryption to MD5
    if (pw==null) return null;
    
    try{
        MessageDigest md =MessageDigest.getInstance("MD5");
        byte[]passBytes=pw.getBytes();
        md.reset();
        byte[] digested = md.digest(passBytes);
        StringBuffer sb =new StringBuffer();
        for (int i=0; i<digested.length;i++){
            sb.append(Integer.toHexString(0xff & digested[i]));
        }
        
        return sb.toString();
    }catch (NoSuchAlgorithmException ex){
        
        
        System.out.println("ERR:NoSuchAlgorithmException > cryptWithMD5");
    }
    
    return null;
    
    }
    
    public G33Create() {
        initComponents();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton_CU = new javax.swing.JButton();
        jButton_DU = new javax.swing.JButton();
        jButton_Back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_CU.setText("Create User");
        jButton_CU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CUActionPerformed(evt);
            }
        });

        jButton_DU.setText("Delete User Record");
        jButton_DU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DUActionPerformed(evt);
            }
        });

        jButton_Back.setText("Back");
        jButton_Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_BackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(210, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_DU, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_CU, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addComponent(jButton_CU, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jButton_DU, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jButton_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_DUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DUActionPerformed
        // TODO add your handling code here:
        		JFileChooser chooser = new JFileChooser("./");
		int status = chooser.showDialog(null, "Select File to Delete"); 
		String orgFile = null;
		if (status == JFileChooser.APPROVE_OPTION)
		orgFile=chooser.getSelectedFile().getName(); //selectedfilename
		
		
		int op =JOptionPane.showConfirmDialog(null,"Do you want to delete this file?","Select an Option...",JOptionPane.YES_NO_CANCEL_OPTION);
		System.out.println(op);

		if(op==0){
		new File(orgFile).delete(); // delete file , or you can just type the file name to replace orgFile if dont want to select
		JOptionPane.showMessageDialog(null, "Delete Finished");
		}
    }//GEN-LAST:event_jButton_DUActionPerformed

    private void jButton_CUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CUActionPerformed
        // TODO add your handling code here:
        String userno = JOptionPane.showInputDialog(null, "How many user do you want to Input?");//let user enter the amount of user.
        int iuserno= Integer.parseInt(userno); //convert string to integer

        String fName = "G33User.CSV";
        String[] in = new String[iuserno+1];//array initiate for input the amount of user record

        in[0]="U_ID,Encrypted_Pwd,U_Name,U_Role,Year_of_birth,Remarks";//asign array i[0] to title

        for(int i=0; i<iuserno; i++){//input the user infor to csv
            G33Create ac = G33Create.usercreates();//output to .CSV file      
            in[i+1]=ac.getinfo();//array to storage each input data
            ac.WriteTextFile(fName, in);//write data inside array to csv
        }
        String[] reContent = G33Create.readTextFile(fName);// read file
               for(int a=0; a<reContent.length; a++)
                  System.out.println("Line"+ a +" "+reContent[a]);
        
        
    }//GEN-LAST:event_jButton_CUActionPerformed

    private void jButton_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_BackActionPerformed
        // TODO add your handling code here:
        new G33Menu_admin().setVisible(true);
        super.dispose();
    }//GEN-LAST:event_jButton_BackActionPerformed

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
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new G33Create().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Back;
    private javax.swing.JButton jButton_CU;
    private javax.swing.JButton jButton_DU;
    // End of variables declaration//GEN-END:variables
}
