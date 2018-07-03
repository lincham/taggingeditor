package taggingtools;

import java.util.LinkedList;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextContent01 {
	private PlainDocument pd;
	LinkedList<String> list  = null;
	public TextContent01(PlainDocument pd) {
		this.pd = pd;
	}
	public void getText() {
		
	}
	
	public void transform() throws BadLocationException {
		String text = pd.getText(0, pd.getLength());		
	}
}
