package taggingtools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

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
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;



public class TaggingEditor {
	private JFrame f;/////////
	private Box pNorth;//修改成Box
	private JScrollPane sp;
	private JTextArea ta;
	private JMenuBar mb;
	private JMenu model,edit,openFileItem,inputTag;
	private JMenuItem chunkTag, nerTag,newFileItem,exitItem,copyItem,pasteItem,conllItem,cBankItem,forChunk,forNer;	
	private JButton openFile,saveFile, chunkParse,setProp;
	
	private JPopupMenu popMenu;
	
	private File file;
	boolean flag = false;
	boolean propChanged = false;
	private ArrayList<JMenuItem> menuList;//右键菜单
	private ArrayList<String> list ;//存放读进来的数据
	//private String fileFormat;
	
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
		//final int UNDO_COUNT = 10;//可撤销次数
		
		menuList = new ArrayList<>();
		list = new ArrayList<>();
		//list = new ArrayList<>();
		f = new JFrame("标注工具");
		f.setBounds(xLocation, yLocation, width, height);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//将frame窗口的叉设置为关闭
		
		pNorth = Box.createHorizontalBox();//Box容器默认采用BoxLayout
		ta = new JTextArea();
		sp = new JScrollPane(ta);
		sp.setBounds(new Rectangle(width/2,height));
		//ta.setPreferredSize(new Dimension(width/2,height));	
		ta.setFont(new Font("微软雅黑",Font.PLAIN,16));//设置默认输入字体属性
		ta.setLineWrap(true);		
		
		mb = new JMenuBar();
		model = new JMenu("模式选择");
		edit = new JMenu("编辑");
		inputTag = new JMenu("导入标注方案");
		openFileItem = new JMenu("打开文件");
		
		chunkTag = new JMenuItem("组块标记");
		nerTag = new JMenuItem("命名实体标记");
		exitItem = new JMenuItem("退出");
		
		newFileItem = new JMenuItem("新建");
		copyItem = new JMenuItem("复制");
		pasteItem = new JMenuItem("粘贴");
		
		conllItem = new JMenuItem("CoNLL");
		cBankItem = new JMenuItem("ChunkBank");
		
		forChunk = new JMenuItem("导入组块标签");
		forNer = new JMenuItem("导入ner标签");
		
		
		openFile = new JButton("打开文件");
		saveFile = new JButton("保存文件");
		//chunkParse = new JButton("解    析");
		//setProp = new JButton("标注更改");
		
	
		popMenu = new JPopupMenu();
		Scanner scan=null ;
		try {
			File popFile = new File("./conf/prop.txt");
			scan = new Scanner(popFile);
			while(scan.hasNext()) {
				menuList.add(new JMenuItem(scan.nextLine()));
			}
		} catch(IOException e) {
			
		}finally {
			if(scan!=null)
				scan.close();
		}
		//添加右键菜单
		for(int i =0 ;i<menuList.size();i++) {			
			popMenu.add(menuList.get(i));
		}
		addListenerToPopup();
		
		ta.setComponentPopupMenu(popMenu);//为文本区域TextPane添加右键菜单，不用add方法+事件监听	
		
		
		//添加菜单
		mb.add(model);
		mb.add(edit);
		mb.add(inputTag);
		mb.add(openFileItem);
		
		model.add(chunkTag);
		model.add(nerTag);
		model.add(exitItem);
		
		edit.add(newFileItem);
		edit.add(copyItem);
		edit.add(pasteItem);
		
		openFileItem.add(conllItem);
		openFileItem.add(cBankItem);
		
		inputTag.add(forChunk);
		inputTag.add(forNer);
		
		
		//sp.setViewportView(ta);//向ScrollPane里添加JTextArea
		
		pNorth.add(openFile);
		pNorth.add(Box.createHorizontalStrut(80));
		
		
		pNorth.add(saveFile);
		pNorth.add(Box.createHorizontalStrut(80));
		
		//pNorth.add(chunkParse);
		//pNorth.add(Box.createHorizontalStrut(80));
		
		//pNorth.add(setProp);
		
		f.setJMenuBar(mb);		
		f.add(pNorth,BorderLayout.NORTH);		
		f.add(sp,BorderLayout.CENTER);
		
		
		
		eventsResponse();
		//f.pack();
		f.setVisible(true);			
		
				
	}
	
	public void eventsResponse(){
		
		
		
		
		//为导入组块标注方案添加监听事件
		forChunk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				FileDialog openDia = new FileDialog(f, "打开文件", FileDialog.LOAD);
				openDia.setVisible(true);			
				String filePath = openDia.getDirectory();// 存储文件路径
				String fileName = openDia.getFile(); // 存储文件名
				
				if (filePath == null || fileName == null) {// 是否选择了有效文件
					return;
				}
				File inputFile = new File(filePath,fileName);
				Scanner sc=null;
				try {
					sc = new Scanner(inputFile);
					int number = popMenu.getComponentCount();
					for(int i =0 ;i<number;i++) {	
						popMenu.remove(menuList.get(i));
					}
					menuList.clear();//清空列表，用于存放新的标注方案
					while(sc.hasNext()) {//读取新的标记方案
						menuList.add(new JMenuItem(sc.nextLine()));
					}
					for(int i =0 ;i<menuList.size();i++) {			
						popMenu.add(menuList.get(i));
					}
					JOptionPane.showMessageDialog(null, "导入成功！");
					addListenerToPopup();
					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally {
					if (sc!=null)
						sc.close();
				}
			}
		});
		
		//cBankItem设置监听
		cBankItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = openDialog();//返回文件的绝对路径
				if (fileName != null) {					
					read(fileName);	//读入文件的内容			
					String textContent = readChunkBank();//转换格式
					ta.setText(textContent);
				}
			}
		});
		//设置conll的监听（读取CoNLL文件）
		conllItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = openDialog();//返回文件的绝对路径
				if(fileName != null) {					
					read(fileName);	//读入文件的内容			
					String textContent = readCoNLL();//转换格式
					ta.setText(textContent);				
				}
			}
		});
		
		//新建文件
		newFileItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				if (flag && !ta.getText().trim().equals("")) {	
					JOptionPane.showMessageDialog(null, "请先保存文件！");						
				}else {
					ta.setText("");	
					file = null;					
				}
			}
		});
		
		//复制
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String copyText = ta.getSelectedText();
				
				//写入剪贴板
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable tCopyText = new StringSelection(copyText);
				clip.setContents(tCopyText,null);
			}
		});
		
		//粘贴
		pasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pasteText = "";
				
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				//获取剪贴板中的内容
				Transferable tPasteText = clip.getContents(null); 
				if (tPasteText != null) {
					//检查内容是否是纯文本
					if (tPasteText.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						try {
							pasteText = (String)tPasteText.getTransferData(DataFlavor.stringFlavor);
						}catch(Exception error) {
							//
						}
					}
				}
				ta.replaceSelection(pasteText);
			}
		});
		
		//为右键菜单的各项添加监听器
		/*for(int i = 0;i<menuList.size();i++) {	
			JMenuItem popupHandle = menuList.get(i);
			popupHandle.addMouseListener(new MouseAdapter (){
				public void mousePressed(MouseEvent e){
					String selectedStr = ta.getSelectedText();
					String tagStyle = popupHandle.getText();
					if (selectedStr != null){
						ta.replaceSelection("["+tagStyle+" "+selectedStr+"]");
					}
				}
				
			});
		}*/
		
		
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
					file = new File(fileDir+fileName);
				}
				try{					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(file),"GBK"));
					String text = ta.getText();
					bw.write(text);
					flag = false;
					bw.close();
					JOptionPane.showMessageDialog(null, "保存成功！");
				}catch(IOException ex){
					throw new RuntimeException();
				}
			}				
		});
		
		//为打开文件的按钮添加相应事件
		openFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){					
				if(flag && !ta.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "文件已更改，请先保存！");
				}else {					
					String fullName = openDialog();
					if (fullName != null) {
						
						ta.setText("");// 文件有效，清空文本区域
						
						file = new File(fullName);
						try {
							InputStreamReader isr = new InputStreamReader(new FileInputStream(file),
									getCharset(fullName));// 读取文件并判断文件的编码方式
							BufferedReader br = new BufferedReader(isr);
							StringBuilder sb = new StringBuilder();
							
							String line = null;
							while ((line = br.readLine()) != null) {
								sb.append(line+"\r\n");//将文本内容读入字符串							
								
							}
							String text = sb.toString();
							ta.setText(text);
							br.close();	
						} catch (IOException ex) {
							
						}					
					}
				}			 
				
			}
		});
		
		//为文本内容添加监听
		ta.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e){
				
			}
			public void removeUpdate(DocumentEvent e) {//监控删除				
				flag = true;				
			}
			public void insertUpdate(DocumentEvent e) {//监控插入				
				flag = true;
			}
		});
	}
	//为右键菜单设置监听器
	private void addListenerToPopup() {
		for(int i =0 ;i<menuList.size();i++) {
			JMenuItem popupHandle = menuList.get(i);
			popupHandle.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e){
					String selectedStr = ta.getSelectedText();
					String tagStyle = popupHandle.getText();
					if (selectedStr != null){
						ta.replaceSelection("["+tagStyle+" "+selectedStr+"]");
					}
				}
			});
		}
	}		
	
	
	//打开文件对话框
	private String openDialog(){
		String fullName = null;
		if(flag && !ta.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "文件已更改，请先保存！");
		}else {
			FileDialog openDia = new FileDialog(f, "打开文件", FileDialog.LOAD);
			openDia.setVisible(true);			
			String filePath = openDia.getDirectory();// 存储文件路径
			String fileName = openDia.getFile(); // 存储文件名
			
			if (filePath == null || fileName == null) {// 是否选择了有效文件
				return null;
			}
			fullName = filePath + fileName;
		}
		return fullName;
	}
	
	//转换ChunkBank格式的文件
	private String readChunkBank() {
		StringBuilder formattedText = new StringBuilder();
		for (int i = 0;i<list.size() ;++i)
		{
			String  s = list.get(i);
			if (s.contains("["))//匹配中括号
			{
				int k = formattedText.length();//记录"["的索引，后面插入组块的tag
				formattedText.append(s+" ");
				for (int j = i+1 ;j<list.size() ;++j )//从list[i]往后遍历寻找该组块的结束标记
				{					
					String str = list.get(j);
					if (str.contains("]"))
					{
						String tag = str.substring(str.indexOf(']')+1,str.length());//获取组块标记的符号
						formattedText.insert(k+1,tag+" ");
						formattedText.append(str.substring(0,str.indexOf(']')+1)+" ");//组块标记的前面的一部分包括中括号
						i=j;
						break;//找到组块的结束标记后就跳出该层循环
					}else
						formattedText.append(str+" ");//该集合元素中没有包含组块的结束标记符（即"]"）,直接添加到字符串的结尾
				}
				
			}else {//没有包含中括号（"["）则直接添加到字符串后面
				formattedText.append(s+" ");				
			}
		}
		list.clear();
		return formattedText.toString();
	}
	
	
	//转换CoNLL格式的文件
	private String readCoNLL(){
		StringBuilder formattedText = new StringBuilder();
		for(int i = 0; i < list.size();++i) {
			if(i%3==2)	{
				String str = list.get(i);
				if(str.charAt(0)=='B') {										
					formattedText.append(" ["+str.substring(2, str.length())+" "+list.get(i-2)+"/"+list.get(i-1)+"]");
					}
				if (str.charAt(0)=='I') {
					formattedText.insert(formattedText.length()-1," "+list.get(i-2)+"/"+list.get(i-1));
					}
				if(str.charAt(0)=='O') {
					//formattedText.insert(formattedText.length()-1,list.get(i-2)+"/"+list.get(i-1)+" ");																}
					formattedText.append(" "+list.get(i-2)+"/"+list.get(i-1));
				}
				}
		}
		list.clear();
		return formattedText.toString();
	}
		
	
	//读取文件
	private void read(String fileName) {
		file = new File(fileName);
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file),getCharset(fileName));
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = br.readLine()) != null) {						
				sb.append(line+" ");//将文本内容读入字符串							
			}
			String text = sb.toString();
			addToList(text);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 将文件的内容保存至集合
	 * 
	 * */
	private void addToList(String s) {
		StringTokenizer st = new StringTokenizer(s);
		while(st.hasMoreTokens()) {
			String token =st.nextToken();
			list.add(token);
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
