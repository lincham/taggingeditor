package taggingtools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.TextEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class TaggingEditor {
	private JFrame f;
	private Box pNorth;//修改成Box
	private JScrollPane sp;
	private JTextArea ta;
	private JMenuBar mb;
	private JMenu model;
	private JMenuItem chunkTag, nerTag,exitItem;	
	private JButton openFile,saveFile, chunkParse,setProp;
	
	private JPopupMenu popMenu;
	private JMenuItem np,vp,pp;
	
	private File file;
	int flag = 0;
	boolean propChanged = false;
	
	//构造器
	public TaggingEditor(){		
		init();
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			   e.printStackTrace();
		  }
	}
	
	//初始化方法
	public void init(){
		int xLocation = 300;
		int yLocation = 100;
		int width = 650;
		int height = 600;
		final int UNDO_COUNT = 10;//可撤销次数
		
		f = new JFrame("标注工具");
		f.setBounds(xLocation, yLocation, width, height);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//将frame窗口的叉设置为关闭
		
		pNorth = Box.createHorizontalBox();//Box容器默认采用BoxLayout
		sp = new JScrollPane();
		ta = new JTextArea();
		ta.setPreferredSize(new Dimension(width/2,height));	
		ta.setFont(new Font("微软雅黑",Font.PLAIN,16));//设置默认输入字体属性
		mb = new JMenuBar();
		model = new JMenu("模式选择");
		chunkTag = new JMenuItem("组块标记");
		nerTag = new JMenuItem("命名实体标记");
		exitItem = new JMenuItem("退出");
		
		openFile = new JButton("打开文件");
		saveFile = new JButton("保存文件");
		chunkParse = new JButton("解    析");
		setProp = new JButton("标注更改");
		
	
		popMenu = new JPopupMenu();
		np = new JMenuItem();
		vp = new JMenuItem();
		pp = new JMenuItem();
		setDefaultPopupMenu();//给右键菜单命名
		//添加右键菜单
		popMenu.add(np);
		popMenu.add(vp);
		popMenu.add(pp);
		
		ta.setComponentPopupMenu(popMenu);//为文本区域TextPane添加右键菜单，不用add方法+事件监听	
		
		
		//添加菜单
		mb.add(model);
		model.add(chunkTag);
		model.add(nerTag);
		model.add(exitItem);
		
		
		sp.setViewportView(ta);//向ScrollPane里添加TextPane
		
		pNorth.add(openFile);
		pNorth.add(Box.createHorizontalStrut(80));
		
		
		pNorth.add(saveFile);
		pNorth.add(Box.createHorizontalStrut(80));
		
		pNorth.add(chunkParse);
		pNorth.add(Box.createHorizontalStrut(80));
		
		pNorth.add(setProp);
		
		f.setJMenuBar(mb);		//窗口中添加菜单栏，不用add（）方法
		f.add(pNorth,BorderLayout.NORTH);//添加面板作为JTextPane的容器		
		f.add(sp,BorderLayout.CENTER);
		
		
		//创建Document对象
		//Document doc = ta.getDocument();
		
		eventsResponse();
		//f.pack();
		f.setVisible(true);			
		
				
	}
	
	public void eventsResponse(){
		
		//为Document添加可撤销监听器
		//tp.addUndoableEditListener(e ->{
			//
		//	UndoableEdit edit = e.getEdit();
		//});
		//为修改属性文件的按钮添加监听
		setProp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (flag != 0 && flag != 1) {
					JOptionPane.showMessageDialog(null,"文件有修改，请先保存！");					
				}else {					
					try {
						file = new File("./conf/popupmenu.properties");
						FileInputStream fis = new FileInputStream(file);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr);
						String propStr = null;
						while((propStr = br.readLine())!=null) {
							ta.append(propStr+"\r\n");
						}
						br.close();
						propChanged = true;
												
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}finally {
						
					}
				}
			}
		});
		
		//为右键菜单的NP添加鼠标监听
		np.addMouseListener(new MouseAdapter (){
			public void mousePressed(MouseEvent e){
				String selectedStr = ta.getSelectedText();
				String tagStyle = np.getText();
				if (selectedStr != null){
					ta.replaceSelection("["+tagStyle+" "+selectedStr+"]");
				}
			}
			
		});
		//为右键的VP添加监听
		vp.addMouseListener(new MouseAdapter (){
			public void mousePressed(MouseEvent e){
				String selectedStr = ta.getSelectedText();
				String tagStyle = vp.getText();
				if (selectedStr != null){
					ta.replaceSelection("["+tagStyle+" "+selectedStr+"]");
				}
			}
			
		});
		//为右键的pp添加监听
		pp.addMouseListener(new MouseAdapter (){
			public void mousePressed(MouseEvent e){
				String selectedStr = ta.getSelectedText();
				String tagStyle = pp.getText();
				if (selectedStr != null){
					ta.replaceSelection("["+tagStyle+" "+selectedStr+"]");
				}
			}
			
		});
		
		//为保存文件添加事件监听
		saveFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				if (file == null){//文件是否已经存在
					FileDialog saveDia = new FileDialog(f,"保存文件",FileDialog.SAVE);//文件不存在则打开保存对话框，否则直接保存就可
					saveDia.setVisible(true);
					String fileDir = saveDia.getDirectory();
					String fileName = saveDia.getFile();
					if (fileDir==null || fileName==null){//路径和文件名不能为空
						return;
					}
				}
				try{
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));//读入文件
					String text = ta.getText();
					bw.write(text);
					flag = 0;
					bw.close();
				}catch(IOException ex){
					throw new RuntimeException();
				}
				
				if (propChanged) {
					setPopupMenu();//修改标注
				}
			}
		});
		
		//为打开文件的按钮添加相应事件
		openFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){	
				if(flag != 1 && flag!=0) {
					JOptionPane.showMessageDialog(null, "文件已更改，请先保存！");
				}else {
					FileDialog openDia = new FileDialog(f, "打开文件", FileDialog.LOAD);
					openDia.setVisible(true);
					String filePath = openDia.getDirectory();// 存储文件路径
					String fileName = openDia.getFile(); // 存储文件名
					if (filePath == null || fileName == null) {// 是否选择了有效文件
						return;
					}
					ta.setText("");// 文件有效，清空文本区域

					file = new File(filePath,fileName);
					String fullName = filePath + fileName;
					try {
						Document doc = ta.getDocument();
						InputStreamReader isr = new InputStreamReader(new FileInputStream(file),
								getCharset(fullName));// 读取文件并判断文件的编码方式
						BufferedReader br = new BufferedReader(isr);

						String line = null;
						while ((line = br.readLine()) != null) {
							ta.append(line + "\r\n");//向文本区域写入内容
						}
						br.close();
					} catch (IOException ex) {

					}					
				}			 
				
			}
		});
		
		//为文本内容添加监听
		ta.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e){
				
			}
			public void removeUpdate(DocumentEvent e) {
				flag += 1;
			}
			public void insertUpdate(DocumentEvent e) {
				flag += 1;
			}
		});
	}
	
	/*
	 * 初始化鼠标右键菜单
	 * */
	private void setDefaultPopupMenu(){
		File propFile = new File("./conf/popupmenu.properties");
		FileInputStream pf = null;
		try {
			pf = new FileInputStream(propFile);
			Properties p = new Properties();
			p.load(pf);
			np.setText(p.getProperty("np"));
			vp.setText(p.getProperty("vp"));
			pp.setText(p.getProperty("pp"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (pf!=null)
					pf.close();
			}catch(IOException e) {
				
			}
		}
	}
	
	/*
	 * 修改右键菜单
	 * */
	private void setPopupMenu() {
		File propFile = null;
		FileInputStream fis = null;
		try {
			propFile = new File("./conf/popupmenu.properties");
			fis = new FileInputStream(propFile);
			Properties prop = new Properties();
			prop.load(fis);
			np.setText(prop.getProperty("np"));
			vp.setText(prop.getProperty("vp"));
			pp.setText(prop.getProperty("pp"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (fis != null) 
					fis.close();
			}catch(IOException e) {
				
			}
		}
		
	}
	
	/**
	 * 获取文件的编码方式
	 */
	private String getCharset(String fileName) throws IOException {

		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
		int p = (bin.read() << 8) + bin.read();

		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		return code;
	}
	
	public static void main(String[] args){
		new TaggingEditor();
	}
	
	

}
