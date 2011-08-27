import java.net.URL;
import java.net.URLConnection;
import java.util.zip.*;

import java.io.*;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

/*
 * @author Requiem-Reborn
 *
 ***/

public class AutoUpdater implements Runnable {

	public static final String ZIP_URL = "LINKTOCLIENTZIP";
	public static final String VERSION_URL = "LINK TO VERSION";
	public static final String VERSION_FILE = "./version.dat";
	
	private AutoUpdater.GUI g;
	
	public double getCurrentVersion(){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(VERSION_FILE)));
			return Double.parseDouble(br.readLine());
		} catch (Exception e) {
			return 0.1;
		}
	}
	
	public double getNewestVersion(){
		try {
			URL tmp = new URL(VERSION_URL);
			BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
			return Double.parseDouble(br.readLine());
		} catch (Exception e) {
			handleException(e);
			return -1;
		}
	}
	
	private void handleException(Exception e){
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("Please Screenshot this message, and send it to an admin!\r\n\r\n");
		strBuff.append(e.getClass().getName() + " \"" + e.getMessage() + "\"\r\n");
		for(StackTraceElement s : e.getStackTrace())
			strBuff.append(s.toString() + "\r\n");
		alert("Exception [" + e.getClass().getSimpleName() + "]",strBuff.toString(),true);
	}
	
	private void alert(String msg){
		alert("Message",msg,false);
	}
	
	private void alert(String title,String msg,boolean error){
		JOptionPane.showMessageDialog(null,
			   msg,
			   title,
			    (error ? JOptionPane.ERROR_MESSAGE : JOptionPane.PLAIN_MESSAGE));
	}
	
	@Override
	public void run() {
		try{
		double newest = getNewestVersion();
		if(newest > this.getCurrentVersion()){
		//if(true){
			int n = JOptionPane.showConfirmDialog(
				    null,
				    "You are currently on version " + getCurrentVersion() + " of the client\r\n" +
				    "The newest version is " + newest + " would you like to update?",
				    "Update found!",
				    JOptionPane.YES_NO_OPTION);
			if(n == 0){
				g = new AutoUpdater.GUI();
				g.setLocationRelativeTo(null);
				g.setVisible(true);
				updateClient();
				OutputStream out = new FileOutputStream(VERSION_FILE);
				out.write(String.valueOf(newest).getBytes());
				alert("Client has been updated, please restart it!");
				System.exit(0);
			}else{
				alert("Please be aware that the client may not be able to connect!");
			}
		}
		}catch(Exception e){
			handleException(e);
		}
	}
	
	private void updateClient() {
		File clientZip = downloadClient();
		if(clientZip != null){
		unZip(clientZip);
		}
	}
	
	private void unZip(File clientZip) {
		try {
			unZipFile(clientZip,new File("./"));
			clientZip.delete();
		} catch (IOException e) {
			handleException(e);
		}
	}
	
	public void unZipFile(File zipFile,File outFile) throws IOException{
		g.setStatus("Un-zipping \"" + zipFile.getName() + "\"");
		g.setPercent(0);
		
		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
		ZipEntry e;
		long max = 0;
		long curr = 0;
		while((e = zin.getNextEntry()) != null)
			max += e.getSize();
		zin.close();
		ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
		while((e = in.getNextEntry()) != null){
			if(e.isDirectory())
				new File(outFile,e.getName()).mkdirs();
			else{
				FileOutputStream out = new FileOutputStream(new File(outFile,e.getName()));
				byte[] b = new byte[1024];
				int len;
				while((len = in.read(b,0,b.length)) > -1){
					curr += len;
						out.write(b, 0, len);
						g.setPercent((int)((curr * 100) / max));
				}
				out.flush();
				out.close();
			}
		}
	}

	private File downloadClient(){
		g.setStatus("Downloading...");
		File ret = new File("tmp.zip");
		try{
		OutputStream out = new FileOutputStream(ret);
		URLConnection conn = new URL(ZIP_URL).openConnection();
		InputStream in = conn.getInputStream();
		long max = conn.getContentLength();
		long curr = 0;
		byte[] b = new byte[1024];
		int len;
		while((len = in.read(b, 0, b.length)) > -1){
			out.write(b,0,len);
			curr += len;
			g.setPercent((int)((curr * 100) / max));
		}
		out.flush();
		out.close();
		in.close();
		return ret;
		}catch(Exception e){
			handleException(e);
				ret.delete();
			return null;
		}
	}

	public static void main(String[] args){
		AutoUpdater a = new AutoUpdater();
		a.run();
	}
	
	public class GUI extends javax.swing.JFrame {

	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** Creates new form GUI */
	    public GUI() {
	                        try{
	            UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	        }catch(Exception ignored){}
	        initComponents();
	    }

	    /** This method is called from within the constructor to
	     * initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is
	     * always regenerated by the Form Editor.
	     */
	    private void initComponents() {

	        jProgressBar1 = new javax.swing.JProgressBar();
	        jLabel1 = new javax.swing.JLabel();
	        jLabel2 = new javax.swing.JLabel();
	        jLabel3 = new javax.swing.JLabel();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setTitle("Auto-Updater");
	        addWindowListener(new java.awt.event.WindowAdapter() {
	            public void windowClosing(java.awt.event.WindowEvent evt) {
	                formWindowClosing(evt);
	            }
	        });

	        jLabel1.setText("Status:");

	        jLabel2.setText("N/A");

	        jLabel3.setText("0%");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(layout.createSequentialGroup()
	                        .addComponent(jLabel1)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(jLabel2)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 304, Short.MAX_VALUE)
	                        .addComponent(jLabel3))
	                    .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(jLabel3))
	                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        pack();
	    }// </editor-fold>//GEN-END:initComponents

	    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

	    }//GEN-LAST:event_formWindowClosing

	    private int percent = 0;

	    public void setStatus(String s){
	        jLabel2.setText(s);
	    }

	    public String getStatus(){
	        return jLabel2.getText();
	    }

	    public void setPercent(int amount){
	        percent = amount;
	        jLabel3.setText(amount + "%");
	        jProgressBar1.setValue(amount);
	    }

	    public int getPercent(){
	        return percent;
	    }

	    // Variables declaration - do not modify//GEN-BEGIN:variables
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JProgressBar jProgressBar1;
	    // End of variables declaration//GEN-END:variables

	}


}