import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
	DatabaseMetaData dbmd;
	PreparedStatement pst;
	Statement st;

	JPanel backPanel, beginPanel, loginPanel, adminPanel, studentPanel;
	JButton back, logout;

	JLabel adminLabel, studentLabel;
	JButton admin, student;
	JLabel userLabel, pwdLabel, cpwdLabel, pwdMatch;
	JTextField userTF;
	JPasswordField pwdTF, cpwdTF;
	JButton login, signup;
	JPanel sCreatePanel;
	JButton sCreateBtn, sReadBtn, sUpdateBtn, sDeleteBtn, sCreateAtdTable;
	JLabel rollNoLabel, nameLabel, classLabel, sectionLabel, phoneLabel, addressLabel;
	JTextField rollNoTF, nameTF, classTF, sectionTF, phoneTF;
	JTextArea addressTA;
	JButton addStudent, cancel;
	JButton stuBioBtn, attendanceBtn, timetableBtn, marksBtn, feeBtn;
	JPanel stuBioPanel, attendancePanel, timetablePanel, marksPanel, feePanel;
	JButton markAttendance;

	ImageIcon sImgIcon;
	JLabel sImg, sRollNoLabel, sNameLabel, sClassLabel, sSectionLabel, sPhoneLabel, sAddressLabel;
	JTextField sRollNoTF, sNameTF, sClassTF, sSectionTF, sPhoneTF;
	JTextArea sAddressTA;

	DefaultTableModel showAllModel, attendanceModel, timetableModel, marksModel, feeModel;
	JTable showAllTable, attendanceTable, timetableTable, marksTable, feeTable;
	JScrollPane showAllSP, attendanceSP, timetableSP, marksSP, feeSP;

	JPanel c1;
	String userId;

	Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	Calendar cal = Calendar.getInstance();

	int width = (int) size.getWidth();
	int height = (int) size.getHeight();
	int totalNoStu = 1;
	int rollNoVal;
	String nameVal, classVal, sectionVal, phoneVal, addressVal;
	int row, col;

	BackListener backListener;
	LogoutListener logoutListener;
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
		logoutListener = new LogoutListener();
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
		logout.addActionListener(logoutListener);
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
		sCreateAtdTable = new JButton("Create attendance");

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
		sCreateAtdTable.addActionListener(adminListener);

		showAllSP.setBounds(100, 100, 300, 600);
		sCreateBtn.setBounds(450, 100, 150, 25);
		sReadBtn.setBounds(450, 150, 150, 25);
		sUpdateBtn.setBounds(450, 200, 150, 25);
		sDeleteBtn.setBounds(450, 250, 150, 25);
		sCreateAtdTable.setBounds(450, 300, 150, 25);

		adminPanel.add(showAllSP);
		adminPanel.add(sCreateBtn);
		adminPanel.add(sReadBtn);
		adminPanel.add(sUpdateBtn);
		adminPanel.add(sDeleteBtn);
		adminPanel.add(sCreateAtdTable);

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
		cancel.addActionListener(this);

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

		Border studentBorder = BorderFactory.createLineBorder(Color.BLACK);

		stuBioBtn.addActionListener(studentListener);
		attendanceBtn.addActionListener(studentListener);
		timetableBtn.addActionListener(studentListener);
		marksBtn.addActionListener(studentListener);
		feeBtn.addActionListener(studentListener);

		stuBioBtn.setBounds(50, 100, 300, 100);
		attendanceBtn.setBounds(50, 200, 300, 100);
		timetableBtn.setBounds(50, 300, 300, 100);
		marksBtn.setBounds(50, 400, 300, 100);
		feeBtn.setBounds(50, 500, 300, 100);

		studentPanel.add(stuBioBtn);
		studentPanel.add(attendanceBtn);
		studentPanel.add(timetableBtn);
		studentPanel.add(marksBtn);
		studentPanel.add(feeBtn);

		// stuBio panel
		stuBioPanel = new JPanel();

		sImgIcon = new ImageIcon("C:\\Users\\sanja\\OneDrive\\Pictures\\Screenshots\\wallpaper.jpg");
		sImg = new JLabel(sImgIcon);
		sRollNoLabel = new JLabel("Roll No :");
		sNameLabel = new JLabel("Name :");
		sClassLabel = new JLabel("Class :");
		sSectionLabel = new JLabel("Section :");
		sPhoneLabel = new JLabel("Phone :");
		sAddressLabel = new JLabel("Address :");
		sRollNoTF = new JTextField(20);
		sNameTF = new JTextField(20);
		sClassTF = new JTextField(20);
		sSectionTF = new JTextField(20);
		sPhoneTF = new JTextField(20);
		sAddressTA = new JTextArea();

		sImg.setBounds(850, 50, 100, 125);
		sRollNoLabel.setBounds(50, 50, 100, 25);
		sRollNoTF.setBounds(200, 50, 200, 25);
		sNameLabel.setBounds(50, 100, 100, 25);
		sNameTF.setBounds(200, 100, 200, 25);
		sClassLabel.setBounds(50, 150, 100, 25);
		sClassTF.setBounds(200, 150, 200, 25);
		sSectionLabel.setBounds(50, 200, 100, 25);
		sSectionTF.setBounds(200, 200, 200, 25);
		sPhoneLabel.setBounds(50, 250, 100, 25);
		sPhoneTF.setBounds(200, 250, 200, 25);
		sAddressLabel.setBounds(50, 300, 100, 25);
		sAddressTA.setBounds(200, 300, 200, 100);

		sRollNoTF.setEditable(false);
		sNameTF.setEditable(false);
		sClassTF.setEditable(false);
		sSectionTF.setEditable(false);
		sPhoneTF.setEditable(false);
		sAddressTA.setEditable(false);

		// stuBioPanel.add(sImg);
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
		stuBioPanel.setBounds(500, 100, width - 500 - 50, 600);
		stuBioPanel.setVisible(true);
		studentPanel.add(stuBioPanel);

		// attendance panel
		attendancePanel = new JPanel();

		markAttendance = new JButton("present");

		attendanceModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		attendanceModel.addColumn("Dates");
		attendanceModel.addColumn("Status");
		attendanceTable = new JTable(attendanceModel);
		attendanceSP = new JScrollPane(attendanceTable);

		attendanceSP.setBounds(25, 100, width - 600, 200);

		markAttendance.addActionListener(studentListener);

		markAttendance.setBounds(10, 10, 100, 25);

		attendancePanel.add(attendanceSP);
		attendancePanel.add(markAttendance);

		attendancePanel.setBorder(studentBorder);
		attendancePanel.setLayout(null);
		attendancePanel.setBounds(500, 100, width - 500 - 50, 600);
		attendancePanel.setVisible(false);
		studentPanel.add(attendancePanel);

		// timetable panel
		timetablePanel = new JPanel();

		timetableModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		timetableModel.addColumn("Day");
		timetableModel.addColumn("P1");
		timetableModel.addColumn("P2");
		timetableModel.addColumn("P3");
		timetableModel.addColumn("P4");
		timetableModel.addColumn("P5");
		timetableModel.addColumn("P6");
		timetableModel.addColumn("P7");
		timetableModel.addColumn("P8");
		timetableTable = new JTable(timetableModel);
		timetableSP = new JScrollPane(timetableTable);

		timetableSP.setBounds(25, 100, width - 600, 200);

		timetablePanel.add(timetableSP);

		timetablePanel.setBorder(studentBorder);
		timetablePanel.setLayout(null);
		timetablePanel.setBounds(500, 100, width - 500 - 50, 600);
		timetablePanel.setVisible(false);
		studentPanel.add(timetablePanel);

		// marks panel
		marksPanel = new JPanel();

		marksModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		marksModel.addColumn("Subject");
		marksModel.addColumn("Marks");
		marksTable = new JTable(marksModel);
		marksSP = new JScrollPane(marksTable);

		marksSP.setBounds(25, 100, width - 600, 200);

		marksPanel.add(marksSP);

		marksPanel.setBorder(studentBorder);
		marksPanel.setLayout(null);
		marksPanel.setBounds(500, 100, width - 500 - 50, 600);
		marksPanel.setVisible(false);
		studentPanel.add(marksPanel);

		// fee panel
		feePanel = new JPanel();

		feeModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		feeModel.addColumn("fee");
		feeModel.addColumn("Paid");
		feeTable = new JTable(feeModel);

		feeTable.setBounds(25, 100, width - 600, 200);

		feePanel.add(feeTable);

		feePanel.setBorder(studentBorder);
		feePanel.setLayout(null);
		feePanel.setBounds(500, 100, width - 500 - 50, 600);
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
			dbmd = con.getMetaData();
			st = con.createStatement();
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

	private void fillShowAllTable() {
		try {
			ResultSet rs = st.executeQuery("select rollno,name from student order by rollno");
			while (rs.next()) {
				int rollno = rs.getInt(1);
				String name = rs.getString(2);
				showAllModel.addRow(new Object[] { rollno, name });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateShowAllTable() {
		showAllModel.setRowCount(0);
		fillShowAllTable();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String s = e.getActionCommand();
		Object o = e.getSource();

		// current user : 0-admin 2-student
		if (o == admin) {
			currentUser = 0;
			LoginVisibility(true);
			this.setVisibility(beginPanel, loginPanel);
			LoginVisibility(true);
		}
		if (o == student) {
			currentUser = 2;
			LoginVisibility(true);
			this.setVisibility(beginPanel, loginPanel);
		}

		if (o == signup && s.equals("Signup")) {
			cpwdLabel.setVisible(true);
			cpwdTF.setVisible(true);
			login.setText("Signup");
			signup.setText("Login");
			clearLogin();
			if (currentUser == 2) {
				userTF.setEditable(false);
				calcTotalNoStu();
				userTF.setText(Integer.toString(totalNoStu));
			}
		}
		if (o == signup && s.equals("Login")) {
			cpwdLabel.setVisible(false);
			cpwdTF.setVisible(false);
			login.setText("Login");
			signup.setText("Signup");
			userTF.setText("");
			userTF.setEditable(true);
			pwdMatch.setText("");
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
							updateShowAllTable();
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
							userId = userTF.getText().trim();
							studentListener.fillstuBioPanel();
							stuBioBtn.doClick();
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
					JOptionPane.showMessageDialog(this, "Signedup successfully!");
					logout.doClick();
				} else if (currentUser == 2) {
					st.executeUpdate("insert into studentlogin values('" + user + "','123')");
					st.executeUpdate("insert into student values(" + user + ",null,null,null,null,null)");
					createAttendanceTable(Integer.parseInt(user));
					JOptionPane.showMessageDialog(this, "Signedup successfully!");
					logout.doClick();
				} else {
					JOptionPane.showMessageDialog(this, "Signup Unsuccessful!");
				}
				cpwdLabel.setVisible(false);
				cpwdTF.setVisible(false);
				userTF.setEditable(true);
				userTF.setText("");
				pwdTF.setText("");
				login.setText("Login");
				signup.setText("Signup");
				pwdMatch.setVisible(false);
				logout.doClick();
				clearLogin();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (o == cancel) {
			if (currentUser == 0) {
				sCreatePanel.setVisible(false);
				adminListener.clearAdminFields();
			} else
				System.out.println("cancel");
		}
	}

	private void clearLogin() {
		userTF.setText("");
		pwdTF.setText("");
		cpwdTF.setText("");
	}

	private void LoginVisibility(boolean b) {
		userLabel.setVisible(b);
		userTF.setVisible(b);
		pwdLabel.setVisible(b);
		pwdTF.setVisible(b);
		login.setVisible(b);
		signup.setVisible(b);
	}

	private void setFieldVisibility(boolean b) {
		nameTF.setEditable(b);
		classTF.setEditable(b);
		sectionTF.setEditable(b);
		phoneTF.setEditable(b);
		addressTA.setEditable(b);
	}

	class LogoutListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (currentUser == 0) {
				cancel.doClick();
				LoginVisibility(false);
				setVisibility(adminPanel, beginPanel);
			} else if (currentUser == 2) {
				cancel.doClick();
				LoginVisibility(false);
				setVisibility(studentPanel, beginPanel);
			}
		}

	}

	class BackListener implements ActionListener {

		HashMap<JPanel, JPanel> backMap = new HashMap<>();
		JPanel currentPanel, newPanel;

		public void load() {
			backMap.put(loginPanel, beginPanel);
			c1 = stuBioPanel;
		}

		public JPanel updateCurrentPanel(JPanel currPanel) {
			currentPanel = currPanel;
			return currentPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LoginVisibility(false);
			newPanel = backMap.get(currentPanel);
			updateCurrentPanel(newPanel);
			setVisibility(currentPanel, newPanel);
			clearLogin();
		}

	}

	private void createAttendanceTable(int n) {
		try {
			ResultSet rs = dbmd.getTables(null, null, "atd" + n, null);
			if (!rs.next()) {
				st.executeQuery("create table atd" + n + " (dates date,status varchar(1))");
				st.executeQuery("insert into atd" + n + " values('12-dec-12','x')");
				JOptionPane.showMessageDialog(this, "Attendance created");
			} else {
				JOptionPane.showMessageDialog(this, "Attendance exists");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Attendance exists");
			try {
				st.executeQuery("delete * from atd"+n+" where staus!='x'");
			} catch (Exception e1) {
				
			}
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
					st.executeUpdate("insert into studentlogin values('" + rollNoTF.getText() + "','123')");
					ResultSet rs = st.executeQuery(q);
					createAttendanceTable(rollNoVal);
					clearAdminFields();
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
					clearAdminFields();
					cancel.doClick();
					updateShowAllTable();
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
				if (o == sCreateAtdTable) {
					int rollNo = (int) showAllTable.getValueAt(row, 0);
					createAttendanceTable(rollNo);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		private void clearAdminFields() {
			// TODO Auto-generated method stub
			rollNoTF.setText("");
			nameTF.setText("");
			classTF.setText("");
			sectionTF.setText("");
			phoneTF.setText("");
			addressTA.setText("");
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
				ResultSet rs1 = st.executeQuery("delete from student where rollno=" + rn);
				ResultSet rs2 = st.executeQuery("drop table atd" + rn);
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

	private void checkAttendance(int userId) {
		String df = new SimpleDateFormat("dd-MMM-yy").format(cal.getTime());
		try {
			ResultSet rs = st.executeQuery("select * from atd" + userId + " where dates='" + df + "'");
			if (rs.next()) {
				markAttendance.setVisible(false);
			} else {
				markAttendance.setVisible(true);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public class StudentListener implements ActionListener {

		boolean ab = false, tb = false, mb = false, fb = false;
		String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
		int i = 0;

		public void fillstuBioPanel() {
			try {
				ResultSet rs = st.executeQuery("select * from student where rollno=" + userId);
				while (rs.next()) {
					sRollNoTF.setText("" + rs.getInt(1));
					sNameTF.setText(rs.getString(2));
					sClassTF.setText(rs.getString(3));
					sSectionTF.setText(rs.getString(4));
					sPhoneTF.setText(rs.getString(5));
					sAddressTA.setText(rs.getString(6));
					checkAttendance(rs.getInt(1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void clearStudentFields() {
			sRollNoTF.setText("");
			sNameTF.setText("");
			sClassTF.setText("");
			sSectionTF.setText("");
			sPhoneTF.setText("");
			sAddressTA.setText("");
			((DefaultTableModel) attendanceTable.getModel()).setRowCount(0);
			((DefaultTableModel) timetableTable.getModel()).setRowCount(0);
			((DefaultTableModel) marksTable.getModel()).setRowCount(0);
			((DefaultTableModel) feeTable.getModel()).setRowCount(0);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String s = e.getActionCommand();
			Object o = e.getSource();

			if (o == stuBioBtn) {
				c1.setVisible(false);
				stuBioPanel.setVisible(true);
				c1 = stuBioPanel;
			}
			if (o == attendanceBtn) {
				c1.setVisible(false);
				if (!ab)
					try {
						ResultSet rs = st.executeQuery("select * from atd" + userId + " where status!='x'");
						while (rs.next()) {
							Date date = rs.getDate(1);
							String status = rs.getString(2);
							attendanceModel.addRow(new Object[] { date.toString(), status });
						}
						ab=true;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				attendancePanel.setVisible(true);
				c1 = attendancePanel;
			}
			if (o == markAttendance) {
				String df = new SimpleDateFormat("dd-MMM-yy").format(cal.getTime());
				try {
					ResultSet rs = st.executeQuery("insert into atd" + userId + " values('" + df + "','p')");
					markAttendance.setVisible(false);
					ab=false;
					attendanceBtn.doClick();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (o == timetableBtn) {
				c1.setVisible(false);

				if (!tb)
					try {
						ResultSet rs = st.executeQuery("select * from timetable");
						while (rs.next()) {
							timetableModel.addRow(new Object[] { days[i], rs.getString(1), rs.getString(2),
									rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),
									rs.getString(8) });
							i++;
						}
						tb = true;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				timetablePanel.setVisible(true);
				c1 = timetablePanel;
			}
			if (o == marksBtn) {
				c1.setVisible(false);

				if (!mb)
					try {
						ResultSet rs = st.executeQuery("select * from marks where rollno=" + userId);
						while (rs.next()) {
							marksModel.addRow(new Object[] { "Tamil", rs.getString(2) });
							marksModel.addRow(new Object[] { "English", rs.getString(3) });
							marksModel.addRow(new Object[] { "Maths", rs.getString(4) });
							marksModel.addRow(new Object[] { "Science", rs.getString(5) });
							marksModel.addRow(new Object[] { "Social", rs.getString(6) });
						}
						mb = true;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				marksPanel.setVisible(true);
				c1 = marksPanel;
			}
			if (o == feeBtn) {
				c1.setVisible(false);

				if (!fb)
					try {
						ResultSet rs = st.executeQuery("select * from fee where rollno=" + userId);
						while (rs.next()) {
							feeModel.addRow(new Object[] { "Paid", rs.getInt(2) });
							feeModel.addRow(new Object[] { "Due", rs.getInt(3) });
						}
						fb = true;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				feePanel.setVisible(true);
				c1 = feePanel;
			}
		}

	}

}
