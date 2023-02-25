import java.util.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.table.*;

public class StudentSystem extends JFrame implements ActionListener {

	public static void main(String args[]) {
		new StudentSystem();
	}

	Connection con;
	PreparedStatement pst;
	Statement st;

	JPanel backPanel, beginPanel, loginPanel, adminPanel, studentPanel;
	JButton back, logout;
	JLabel adminLabel, studentLabel;
	JButton admin, student;
	JLabel userLabel,pwdLabel, cpwdLabel, pwdMatch;
	JTextField userTF; 
	JPasswordField pwdTF, cpwdTF;
	JButton login, signup;
	JPanel sCreatePanel;
	JButton sCreateBtn, sReadBtn, sUpdateBtn, sDeleteBtn;
	JLabel rollNoLabel, nameLabel, classLabel, sectionLabel, phoneLabel, addressLabel;
	JTextField rollNoTF, nameTF, classTF, sectionTF, phoneTF;
	JTextArea addressTA;
	JButton addStudent, cancel;
	JButton stuBioBtn, attendanceBtn, timetableBtn, marksBtn, feeBtn;
	JPanel stuBioPanel, attendancePanel, timetablePanel, marksPanel, feePanel;
	
	JLabel sImg,sRollNoLabel,sNameLabel,sClassLabel,sSectionLabel,sPhoneLabel,sAddressLabel;
	JTextField sRollNoTF,sNameTF,sClassTF,sSectionTF,sPhoneTF;
	JTextArea sAddressTA;

	DefaultTableModel showAllModel;
	JTable showAllTable;
	JScrollPane showAllSP;

	Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	int width = (int) size.getWidth();
	int height = (int) size.getHeight();
	int totalNoStu = 1;
	int rollNoVal;
	String nameVal, classVal, sectionVal, phoneVal, addressVal;
	int row, col;

	BackListener backListener;
	AdminListener adminListener;
	StudentListener studentListener;

	int currentUser;

	StudentSystem() {
		// frame
		super("Student Management System");
		setBackground(Color.BLUE);
		setLayout(new BorderLayout());
		setSize(width, height);

		connectJDBC();

		System.out.println(width + " " + height);

		backListener = new BackListener();
		adminListener = new AdminListener();
		studentListener = new StudentListener();

		// back panel
		backPanel = new JPanel();

		back = new JButton("back");
		back.addActionListener(backListener);
		back.setVisible(false);
		back.setBounds(5, 5, 100, 30);
		backPanel.add(back);

		logout = new JButton("logout");
		logout.addActionListener(adminListener);
		logout.setVisible(false);
		logout.setBounds(width - 105, 5, 100, 30);
		backPanel.add(logout);

		backPanel.setLayout(null);
		backPanel.setSize(width, 40);
		backPanel.setVisible(true);
		add(backPanel, BorderLayout.CENTER);

		// begin panel
		beginPanel = new JPanel();

		adminLabel = new JLabel("ADMIN");
		studentLabel = new JLabel("STUDENT");

		ImageIcon adminIcon = new ImageIcon("images/admin.png");
		Image adminImg = adminIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
		admin = new JButton(new ImageIcon(adminImg));

		ImageIcon studentIcon = new ImageIcon("images/student.png");
		Image studentImg = studentIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
		student = new JButton(new ImageIcon(studentImg));

		admin.addActionListener(this);
		student.addActionListener(this);

		admin.setBounds(450, 200, 250, 250);
		student.setBounds(800, 200, 250, 250);
		adminLabel.setBounds(510, 480, 250, 50);
		studentLabel.setBounds(850, 480, 250, 50);

		adminLabel.setFont(new Font("Serif", Font.BOLD, 30));
		studentLabel.setFont(new Font("Serif", Font.BOLD, 30));

		beginPanel.add(admin);
		beginPanel.add(student);
		beginPanel.add(adminLabel);
		beginPanel.add(studentLabel);

		beginPanel.setLayout(null);
		beginPanel.setSize(width, height);
		beginPanel.setVisible(true);
		add(beginPanel);

		// login panel
		loginPanel = new JPanel();

		userLabel = new JLabel("Username");
		pwdLabel = new JLabel("Password");
		cpwdLabel = new JLabel("Confirm Password");
		pwdMatch = new JLabel("");
		userTF = new JTextField(20);
		pwdTF = new JPasswordField(20);
		cpwdTF = new JPasswordField(20);
		login = new JButton("Login");
		signup = new JButton("Signup");

		login.addActionListener(this);
		signup.addActionListener(this);
		cpwdTF.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				warn();
			}

			public void removeUpdate(DocumentEvent e) {
				warn();
			}

			public void insertUpdate(DocumentEvent e) {
				warn();
			}

			public void warn() {
				if (pwdTF.equals("") || cpwdTF.equals(""))
					pwdMatch.setText("");
				else if (!(pwdTF.getText()).equals(cpwdTF.getText())) {
					pwdMatch.setForeground(Color.RED);
					pwdMatch.setText("Password doesn't match");
				} else {
					pwdMatch.setForeground(Color.GREEN);
					pwdMatch.setText("Password match");
				}
			}
		});

		userLabel.setBounds(400, 200, 200, 30);
		pwdLabel.setBounds(400, 250, 200, 30);
		cpwdLabel.setBounds(400, 300, 200, 30);
		userTF.setBounds(600, 200, 200, 30);
		pwdTF.setBounds(600, 250, 200, 30);
		cpwdTF.setBounds(600, 300, 200, 30);
		pwdMatch.setBounds(820, 300, 200, 30);
		login.setBounds(550, 400, 100, 30);
		signup.setBounds(550, 450, 100, 30);

		loginPanel.add(userLabel);
		loginPanel.add(pwdLabel);
		loginPanel.add(cpwdLabel);
		loginPanel.add(userTF);
		loginPanel.add(pwdTF);
		loginPanel.add(cpwdTF);
		loginPanel.add(pwdMatch);
		loginPanel.add(login);
		loginPanel.add(signup);

		cpwdLabel.setVisible(false);
		cpwdTF.setVisible(false);

		loginPanel.setLayout(null);
		loginPanel.setSize(width, height);
		loginPanel.setVisible(false);
		add(loginPanel);

		// admin panel
		adminPanel = new JPanel();

		sCreateBtn = new JButton("Add Student");
		sReadBtn = new JButton("Read Details");
		sUpdateBtn = new JButton("update Student");
		sDeleteBtn = new JButton("Delete Student");

		showAllModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		showAllModel.addColumn("RollNo");
		showAllModel.addColumn("Name");
		showAllTable = new JTable(showAllModel);
		showAllSP = new JScrollPane(showAllTable);
		showAllTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				final JTable target = (JTable) e.getSource();
				final int row = target.getSelectedRow();
				final int column = target.getSelectedColumn();
				updateRowCol(row, column);
			}
		});

		sCreateBtn.addActionListener(adminListener);
		sReadBtn.addActionListener(adminListener);
		sUpdateBtn.addActionListener(adminListener);
		sDeleteBtn.addActionListener(adminListener);

		showAllSP.setBounds(100, 100, 300, 600);
		sCreateBtn.setBounds(450, 100, 150, 25);
		sReadBtn.setBounds(450, 150, 150, 25);
		sUpdateBtn.setBounds(450, 200, 150, 25);
		sDeleteBtn.setBounds(450, 250, 150, 25);

		adminPanel.add(showAllSP);
		adminPanel.add(sCreateBtn);
		adminPanel.add(sReadBtn);
		adminPanel.add(sUpdateBtn);
		adminPanel.add(sDeleteBtn);

		adminPanel.setLayout(null);
		adminPanel.setSize(width, height);
		adminPanel.setVisible(false);
		add(adminPanel);

		// add Student panel
		sCreatePanel = new JPanel();

		rollNoLabel = new JLabel("Roll No :");
		nameLabel = new JLabel("Name :");
		classLabel = new JLabel("Class :");
		sectionLabel = new JLabel("Section :");
		phoneLabel = new JLabel("Phone :");
		addressLabel = new JLabel("Address :");
		rollNoTF = new JTextField(20);
		nameTF = new JTextField(20);
		classTF = new JTextField(20);
		sectionTF = new JTextField(20);
		phoneTF = new JTextField(20);
		addressTA = new JTextArea();
		addStudent = new JButton("Add Student");
		cancel = new JButton("Cancel");

		rollNoTF.setEditable(false);
		addStudent.addActionListener(adminListener);
		cancel.addActionListener(adminListener);

		rollNoLabel.setBounds(50, 50, 100, 25);
		rollNoTF.setBounds(200, 50, 200, 25);
		nameLabel.setBounds(50, 100, 100, 25);
		nameTF.setBounds(200, 100, 200, 25);
		classLabel.setBounds(50, 150, 100, 25);
		classTF.setBounds(200, 150, 200, 25);
		sectionLabel.setBounds(50, 200, 100, 25);
		sectionTF.setBounds(200, 200, 200, 25);
		phoneLabel.setBounds(50, 250, 100, 25);
		phoneTF.setBounds(200, 250, 200, 25);
		addressLabel.setBounds(50, 300, 100, 25);
		addressTA.setBounds(200, 300, 200, 100);
		addStudent.setBounds(50, 450, 150, 25);
		cancel.setBounds(350, 450, 150, 25);

		sCreatePanel.add(rollNoLabel);
		sCreatePanel.add(rollNoTF);
		sCreatePanel.add(nameLabel);
		sCreatePanel.add(nameTF);
		sCreatePanel.add(classLabel);
		sCreatePanel.add(classTF);
		sCreatePanel.add(sectionLabel);
		sCreatePanel.add(sectionTF);
		sCreatePanel.add(phoneLabel);
		sCreatePanel.add(phoneTF);
		sCreatePanel.add(addressLabel);
		sCreatePanel.add(addressTA);
		sCreatePanel.add(addStudent);
		sCreatePanel.add(cancel);

		sCreatePanel.setLayout(null);
		sCreatePanel.setBounds(700, 100, 500, 600);
		sCreatePanel.setVisible(false);
		adminPanel.add(sCreatePanel);

		// student panel
		studentPanel = new JPanel();

		stuBioBtn = new JButton("Bio");
		attendanceBtn = new JButton("Attendance");
		timetableBtn = new JButton("TimeTable");
		marksBtn = new JButton("Marks");
		feeBtn = new JButton("Fee");
		
		Border studentBorder=BorderFactory.createLineBorder(Color.BLACK);

		stuBioBtn.addActionListener(studentListener);
		attendanceBtn.addActionListener(studentListener);
		timetableBtn.addActionListener(studentListener);
		marksBtn.addActionListener(studentListener);
		feeBtn.addActionListener(studentListener);
		
		stuBioBtn.setBounds(50,100,300,100);
		attendanceBtn.setBounds(50,200,300,100);
		timetableBtn.setBounds(50,300,300,100);
		marksBtn.setBounds(50,400,300,100);
		feeBtn.setBounds(50,500,300,100);
		
		studentPanel.add(stuBioBtn);
		studentPanel.add(attendanceBtn);
		studentPanel.add(timetableBtn);
		studentPanel.add(marksBtn);
		studentPanel.add(feeBtn);
		
		// stuBio panel
		stuBioPanel=new JPanel();
		
		sRollNoLabel=new JLabel("Roll No :");
		sNameLabel=new JLabel("Name :");
		sClassLabel=new JLabel("Class");
		sSectionLabel=new JLabel("Section");
		sPhoneLabel=new JLabel("Phone");
		sAddressLabel=new JLabel("Address");
		sRollNoTF=new JTextField(20);
		sNameTF=new JTextField(20);
		sClassTF=new JTextField(20);
		sSectionTF=new JTextField(20);
		sPhoneTF=new JTextField(20);
		sAddressTA=new JTextArea();
		
		sRollNoLabel.setBounds(50,50,100,25);
		sRollNoTF.setBounds(200,50,200,25);
		
		
		stuBioPanel.add(sRollNoLabel);
		stuBioPanel.add(sNameLabel);
		stuBioPanel.add(sClassLabel);
		stuBioPanel.add(sSectionLabel);
		stuBioPanel.add(sPhoneLabel);
		stuBioPanel.add(sAddressLabel);
		stuBioPanel.add(sRollNoTF);
		stuBioPanel.add(sNameTF);
		stuBioPanel.add(sClassTF);
		stuBioPanel.add(sSectionTF);
		stuBioPanel.add(sPhoneTF);
		stuBioPanel.add(sAddressTA);
		
		stuBioPanel.setBorder(studentBorder);
		stuBioPanel.setLayout(null);
		stuBioPanel.setBounds(500,100,width-500-50,600);
		stuBioPanel.setVisible(true);
		studentPanel.add(stuBioPanel);
		
		// attendance panel
		attendancePanel=new JPanel();
		
		attendancePanel.setBorder(studentBorder);
		attendancePanel.setLayout(null);
		attendancePanel.setBounds(500,100,width-500-50,600);
		attendancePanel.setVisible(false);
		studentPanel.add(attendancePanel);
		
		// timetable panel
		timetablePanel=new JPanel();
		
		timetablePanel.setBorder(studentBorder);
		timetablePanel.setLayout(null);
		timetablePanel.setBounds(500,100,width-500-50,600);
		timetablePanel.setVisible(false);
		studentPanel.add(timetablePanel);
		
		// marks panel
		marksPanel=new JPanel();
		
		marksPanel.setBorder(studentBorder);
		marksPanel.setLayout(null);
		marksPanel.setBounds(500,100,width-500-50,600);
		marksPanel.setVisible(false);
		studentPanel.add(marksPanel);
		
		//fee panel
		feePanel=new JPanel();
		
		feePanel.setBorder(studentBorder);
		feePanel.setLayout(null);
		feePanel.setBounds(500,100,width-500-50,600);
		feePanel.setVisible(false);
		studentPanel.add(feePanel);

		studentPanel.setLayout(null);
		studentPanel.setSize(width, height);
		studentPanel.setVisible(false);
		add(studentPanel);

		// load backMap
		backListener.load();

		// frame
		setVisible(true);
	}

	private void connectJDBC() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@localhost:1521:XE";
			con = DriverManager.getConnection(url, "sanjay", "2001");
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fillShowAllTable() {
		try {
			ResultSet rs = st.executeQuery("select rollno,name from student");
			while (rs.next()) {
				int rollno = rs.getInt(1);
				String name = rs.getString(2);
				showAllModel.addRow(new Object[] { rollno, name });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateRowCol(int row, int column) {
		this.row = row;
		this.col = column;
	}

	private void setVisibility(JPanel off, JPanel on) {
		off.setVisible(false);
		on.setVisible(true);
		backListener.updateCurrentPanel(on);
		if (on == loginPanel) {
			back.setVisible(true);
			logout.setVisible(false);
		} else if (on == adminPanel || on == studentPanel) {
			back.setVisible(false);
			logout.setVisible(true);
		} else {
			back.setVisible(false);
			logout.setVisible(false);
		}
	}

	private void calcTotalNoStu() {
		try {
			ResultSet rs = st.executeQuery("select count(*) from student");
			while (rs.next())
				totalNoStu = Integer.parseInt(rs.getString(1)) + 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearFields() {
		// TODO Auto-generated method stub
		rollNoTF.setText("");
		nameTF.setText("");
		classTF.setText("");
		sectionTF.setText("");
		phoneTF.setText("");
		addressTA.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String s = e.getActionCommand();
		Object o = e.getSource();

		// current user : 0-admin 2-student
		if (o == admin) {
			currentUser = 0;
			this.setVisibility(beginPanel, loginPanel);
		}
		if (o == student) {
			currentUser = 2;
			this.setVisibility(beginPanel, loginPanel);
		}

		if (o == signup && s.equals("Signup")) {
			cpwdLabel.setVisible(true);
			cpwdTF.setVisible(true);
			login.setText("Signup");
			signup.setText("Login");
			clearLogin();
		}
		if (o == signup && s.equals("Login")) {
			cpwdLabel.setVisible(false);
			cpwdTF.setVisible(false);
			login.setText("Login");
			signup.setText("Signup");
			clearLogin();
		}

		if (o == login && s.equals("Login")) { // t-adminlogin,studentlogin
			try {
				if (currentUser == 0) {
					ResultSet rs = st.executeQuery(
							"select password from adminlogin where username='" + userTF.getText().trim() + "'");
					if (rs.next()) {
						if (rs.getString(1).equals(pwdTF.getText().trim())) {
							this.setVisibility(loginPanel, adminPanel);
							fillShowAllTable();
						}
					} else {
						JOptionPane.showMessageDialog(null, "incorrect username/password");
						clearLogin();
					}
				} else if (currentUser == 2) {
					ResultSet rs = st.executeQuery(
							"select password from studentlogin where username='" + userTF.getText().trim() + "'");
					if (rs.next()) {
						if (rs.getString("password").equals(pwdTF.getText().trim())) {
							this.setVisibility(loginPanel, studentPanel);
						}
					} else {
						JOptionPane.showMessageDialog(null, "incorrect username/password");
						clearLogin();
					}
				}
				clearLogin();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
		if (o == login && s.equals("Signup")) {
			String user = userTF.getText();
			String pwd = pwdTF.getText();
			String cpwd = cpwdTF.getText();

			try {
				st = con.createStatement();
				if (currentUser == 0) {
					st.executeUpdate("insert into adminlogin values('" + user + "','" + pwd + "')");
				} else if (currentUser == 2) {
					st.executeUpdate("insert into studentlogin values('" + user + "','" + pwd + "')");
				}
				cpwdLabel.setVisible(false);
				cpwdTF.setVisible(false);
				userTF.setText("");
				pwdTF.setText("");
				login.setText("Login");
				signup.setText("Signup");
				pwdMatch.setVisible(false);
				clearLogin();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void clearLogin() {
		userTF.setText("");
		pwdTF.setText("");
		cpwdTF.setText("");
	}

	private void setFieldVisibility(boolean b) {
		nameTF.setEditable(b);
		classTF.setEditable(b);
		sectionTF.setEditable(b);
		phoneTF.setEditable(b);
		addressTA.setEditable(b);
	}

	class BackListener implements ActionListener {

		JPanel currentPanel, newPanel;
		HashMap<JPanel, JPanel> backMap = new HashMap<>();

		public void load() {
			backMap.put(loginPanel, beginPanel);
		}

		public JPanel updateCurrentPanel(JPanel currentPanel) {
			this.currentPanel = currentPanel;
			return currentPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			newPanel = backMap.get(currentPanel);
			updateCurrentPanel(newPanel);
			setVisibility(currentPanel, newPanel);
		}

	}

	class AdminListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String s = e.getActionCommand();
			Object o = e.getSource();

			calcTotalNoStu();

			try {
				if (o == sCreateBtn) {
					setFieldVisibility(true);
					sCreatePanel.setVisible(true);
					rollNoTF.setText(Integer.toString(totalNoStu));
				}
				if (o == addStudent && s.equals("Add Student")) {
					rollNoVal = Integer.parseInt(rollNoTF.getText().trim());
					nameVal = nameTF.getText().trim();
					classVal = classTF.getText().trim();
					sectionVal = sectionTF.getText().trim();
					phoneVal = phoneTF.getText().trim();
					addressVal = addressTA.getText().trim();
					String q = String.format("insert into student values (%d,'%s','%s','%s','%s','%s')", rollNoVal,
							nameVal, classVal, sectionVal, phoneVal, addressVal);
					ResultSet rs = st.executeQuery(q);
					clearFields();
					updateShowAllTable();
				}
				if (o == addStudent && s.equals("ok")) {
					cancel.doClick();
				}
				if (o == addStudent && s.equals("update")) {
					rollNoVal = Integer.parseInt(rollNoTF.getText().trim());
					nameVal = nameTF.getText().trim();
					classVal = classTF.getText().trim();
					sectionVal = sectionTF.getText().trim();
					phoneVal = phoneTF.getText().trim();
					addressVal = addressTA.getText().trim();
					String q = String.format(
							"update student set name='%s',class='%s',section='%s',phone='%s',address='%s' where rollno=%d",
							nameVal, classVal, sectionVal, phoneVal, addressVal, rollNoVal);
					ResultSet rs = st.executeQuery(q);
					clearFields();
					cancel.doClick();
					updateShowAllTable();
				}
				if (o == cancel) {
					sCreatePanel.setVisible(false);
					clearFields();
				}
				if (o == sReadBtn) {
					setFieldVisibility(false);
					readRow(row);
				}
				if (o == sUpdateBtn) {
					setFieldVisibility(true);
					updateRow(row);
				}
				if (o == sDeleteBtn) {
					setFieldVisibility(false);
					deleteRow(row);
				}
				if (o == logout) {
					cancel.doClick();
					setVisibility(adminPanel, beginPanel);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		private void updateShowAllTable() {
			showAllModel.setRowCount(0);
			fillShowAllTable();
		}

		private void readRow(int row) {
			try {
				int rn = (int) showAllTable.getValueAt(row, 0);
				String q = "select * from student where rollno=" + rn;
				ResultSet rs = st.executeQuery(q);
				if (rs.next())
					readStudentPanel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
							rs.getString(6));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void updateRow(int row) {
			try {
				int rn = (int) showAllTable.getValueAt(row, 0);
				String q = "select * from student where rollno=" + rn;
				ResultSet rs = st.executeQuery(q);
				if (rs.next())
					updateStudentPanel(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
							rs.getString(6));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void deleteRow(int row) {
			try {
				int rn = (int) showAllTable.getValueAt(row, 0);
				ResultSet rs = st.executeQuery("delete from student where rollno=" + rn);
				JOptionPane.showMessageDialog(null, "deleted successfully");
				updateShowAllTable();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void readStudentPanel(int r, String n, String c, String s, String p, String a) {
			rollNoTF.setText("" + r);
			nameTF.setText(n);
			classTF.setText(c);
			sectionTF.setText(s);
			phoneTF.setText(p);
			addressTA.setText(a);

			sCreatePanel.setVisible(true);

			addStudent.setText("ok");

		}

		private void updateStudentPanel(int r, String n, String c, String s, String p, String a) {
			rollNoTF.setText("" + r);
			nameTF.setText(n);
			classTF.setText(c);
			sectionTF.setText(s);
			phoneTF.setText(p);
			addressTA.setText(a);

			sCreatePanel.setVisible(true);

			addStudent.setText("update");
		}

	}

	public class StudentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String s=e.getActionCommand();
			Object o=e.getSource();
			
			if(o==stuBioBtn) {
				stuBioPanel.setVisible(true);
			}
			if(o==attendanceBtn) {
				attendancePanel.setVisible(true);
			}
			if(o==timetableBtn) {
				timetablePanel.setVisible(true);
			}
			if(o==marksBtn) {
				marksPanel.setVisible(true);
			}
			if(o==feeBtn) {
				feePanel.setVisible(true);
			}
		}

	}

}
