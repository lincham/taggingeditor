package taggingtools;

import java.util.LinkedList;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class TextContent {
	private PlainDocument pd;
	LinkedList<String> list  = null;
	public TextContent(PlainDocument pd) {
		this.pd = pd;
	}
	public void getText() {
		
	}
	
	public void transform() throws BadLocationException {
		String text = pd.getText(0, pd.getLength());		
	}
}
