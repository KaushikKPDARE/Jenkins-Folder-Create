import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

/**
 * API to create folders in Jenkins uing cURL
 * @author kaupadma (Kaushik)
 *
 */
public class JenkinsFolderApi {

	private static JTextField username = null;
	private static JTextField password = null;
	private static JTextField configPath = null;
	private static JPanel jPanel = null;

	public JenkinsFolderApi(){
		username = new JTextField(18);
		password = new JTextField(18);
		configPath = new JTextField(18);

		jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		jPanel.setBounds(40, 40, 40, 40);
		jPanel.add(new JLabel("Username: "));
		jPanel.add(username);

		jPanel.add(new JLabel("Password: "));
		jPanel.add(password);

		jPanel.add(new JLabel("Config File Path: "));
		jPanel.add(configPath);
	}

	public boolean folderCreate(String username, String password, String[] folderStruct, String path) {

		String credentials = username + ":" + password;

		for(String i : folderStruct){

			try{

				String checkPath = path + "job/" + i;
				BufferedReader checkBuffer = runProcess(checkPath, credentials, true);

				System.out.println(checkBuffer.readLine()==null ? "Folder exists" : "Folder does not exist");

				if(checkBuffer.readLine() != null){

					String createPath = path + "createItem?name=" + i + "&mode=com.cloudbees.hudson.plugins.folder.Folder&Submit=OK";
					BufferedReader createBuffer = runProcess(createPath, credentials, false);
					System.out.println(createBuffer.readLine()==null ? "Folder Created: " + i : "Error creating folder");

				}

				path = path + "job/" + i + "/";
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}

	public BufferedReader runProcess(String path, String credentials, boolean isPresentCheck){
		BufferedReader buffer = null;
		try {

			ProcessBuilder pr = isPresentCheck ? new ProcessBuilder("curl", "-XGET", path, "--user", credentials) 
					: new ProcessBuilder("curl", "-XPOST", path, "--user", credentials, "-H", "Content-Type:application/x-www-form-urlencoded");
			Process process = pr.start();

			Thread.sleep(1500);
			buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;

	}

	public int getResult(String msg, boolean isAlert){
		return JOptionPane.showConfirmDialog(null, isAlert ? msg : jPanel, isAlert ? "Alert" : "Jenkins Folder API", JOptionPane.OK_CANCEL_OPTION);
	}

	public static void main(String[] args) {

		JenkinsFolderApi jenkins = new JenkinsFolderApi();

		int result = 0;

		result = jenkins.getResult("", false);

		if (result == JOptionPane.OK_OPTION) {

			while(username.getText().equals("") || username.getText()==null){				

				if (jenkins.getResult("Error: username is empty", true) == JOptionPane.OK_OPTION) {
					if (jenkins.getResult("", false) == JOptionPane.OK_OPTION){
						continue;
					}
				} else {
					System.exit(0);
				}
			}

			while(password.getText().equals("") || password.getText()==null){

				if (jenkins.getResult("Error: password is empty", true) == JOptionPane.OK_OPTION) {
					if (jenkins.getResult("", false) == JOptionPane.OK_OPTION){
						continue;
					}
				} else {
					System.exit(0);
				}
			}

			while(configPath.getText().equals("") || configPath.getText()==null){

				if (jenkins.getResult("Error: config file path is empty", true) == JOptionPane.OK_OPTION) {
					if (jenkins.getResult("", false) == JOptionPane.OK_OPTION){
						continue;
					}
				} else {
					System.exit(0);
				}
			}

			System.out.println("Username: " + username.getText() + " Password: " + password.getText() + " Path: " + configPath.getText());

			String[] pathStruct = configPath.getText().split(",");
			boolean isCreated = jenkins.folderCreate(username.getText(), password.getText(), pathStruct, "https://engci-private-sjc.cisco.com/jenkins/cmo/job/Nimbus/job/SS4/view/All/");
			
			if(isCreated){
				JOptionPane.showMessageDialog(null, "Success: folders created");
			} else{
				JOptionPane.showMessageDialog(null, "Failure: folders cannot be created");
			}
		}

		//		JenkinsFolderApi api = new JenkinsFolderApi();
		//		List<String> pathStructure = new ArrayList<String>();
		//		String userName = System.getProperty("username"), password = System.getProperty("password");
		//
		//		String[] pathStruct = System.getProperty("folders").split(",");
		//		String rootPath = System.getProperty("root", "https://engci-private-sjc.cisco.com/jenkins/cmo/job/Nimbus/job/SS4/view/All/");

	}

}
