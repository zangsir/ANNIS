/*
 *  Copyright 2009 thomas.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

/*
 * MainFrame.java
 *
 * Created on 26.10.2009, 16:29:20
 */
package de.hu_berlin.german.korpling.annis.kickstarter;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

/**
 *
 * @author thomas
 */
public class MainFrame extends javax.swing.JFrame
{

  /** Creates new form MainFrame */
  public MainFrame()
  {
    System.setProperty("annis.home", ".");

    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception ex)
    {
      Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
    }

    initComponents();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    btInit = new javax.swing.JButton();
    btImport = new javax.swing.JButton();
    btList = new javax.swing.JButton();
    btStartStop = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Annis² Kickstarter");

    btInit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/hu_berlin/german/korpling/annis/kickstarter/jlfgr/New24.gif"))); // NOI18N
    btInit.setMnemonic('d');
    btInit.setText("Init database");
    btInit.setName("btInit"); // NOI18N
    btInit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btInitActionPerformed(evt);
      }
    });

    btImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/hu_berlin/german/korpling/annis/kickstarter/jlfgr/Import24.gif"))); // NOI18N
    btImport.setMnemonic('i');
    btImport.setText("Import corpus");
    btImport.setName("btImport"); // NOI18N

    btList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/hu_berlin/german/korpling/annis/kickstarter/jlfgr/RowInsertBefore24.gif"))); // NOI18N
    btList.setMnemonic('l');
    btList.setText("List imported corpora");
    btList.setName("btList"); // NOI18N

    btStartStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/hu_berlin/german/korpling/annis/kickstarter/jlfgr/Play24.gif"))); // NOI18N
    btStartStop.setMnemonic('s');
    btStartStop.setText("Start Annis");
    btStartStop.setName("btStartStop"); // NOI18N
    btStartStop.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btStartStopActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(btStartStop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addComponent(btImport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addComponent(btInit, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addComponent(btList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(btInit)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btImport)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btList)
        .addGap(18, 18, 18)
        .addComponent(btStartStop)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void btInitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btInitActionPerformed
    {//GEN-HEADEREND:event_btInitActionPerformed

      InitDialog dlg = new InitDialog(this, true);
      dlg.setVisible(true);

    }//GEN-LAST:event_btInitActionPerformed

    private void btStartStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btStartStopActionPerformed
    {//GEN-HEADEREND:event_btStartStopActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_btStartStopActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    java.awt.EventQueue.invokeLater(new Runnable()
    {

      public void run()
      {
        new MainFrame().setVisible(true);
      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btImport;
  private javax.swing.JButton btInit;
  private javax.swing.JButton btList;
  private javax.swing.JButton btStartStop;
  // End of variables declaration//GEN-END:variables
}
