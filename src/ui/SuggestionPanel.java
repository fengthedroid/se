package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import control.MainControl;
import ui.PatientSearchView.ComboBoxRenderer;


/**
 * 
 * @author Fred
 *
 */
////initially isFired = false;
////keyboard check, if there's space, fire, isFired = true
////keyboard check, if there's still space, do nothing
//// if the space goes away, isFired = false
//
////extend jcombobox with a flag!!!
@SuppressWarnings({"serial","rawtypes"})
public class SuggestionPanel extends VanillaPanel {

	public SuggestionPanel(int width) {
		super(width);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Static factory
	 * @param width
	 * @param height
	 * @return
	 */
	public static JPanel[] fillerWithScroll(int width, int height){
		JPanel[] panels ={null,null};
		panels[0] = new JPanel();
		panels[1] = new SuggestionPanel(width);
		JScrollPane scroll = new JScrollPane(panels[1]);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(width,height));
		panels[0].add(scroll);
		return panels;
	}
	
	/**
	 * create the drugLines
	 * @param s
	 * @param edible
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected JComboBox drugLine(String s,boolean edible){
		JComboBox drug = new FirableBox();
		int width = (int) (boxWidth*.9);
		drug.setPreferredSize(new Dimension(width,30));
		drug.setEditable(true);

		drug.setUI(new BasicComboBoxUI() {
			@Override
			protected JButton createArrowButton() {
				JButton b = new JButton() {
					@Override
					public int getWidth() {
						return 0;
					}
				};
				b.setVisible(false);
				return b;
			}
		});

		drug.setRenderer(new ComboBoxRenderer());
		SearchBoxModel sbm = new SearchBoxModel(drug,MainControl.getMainControl().getFormulary().getAllDrugSet());
		drug.setModel(sbm);
		drug.addItemListener(sbm);
		//JTextField must under model or not working
		JTextField jt = (JTextField)drug.getEditor().getEditorComponent();
		jt.setBorder(BorderFactory.createEmptyBorder(2,10, 2, 2));
		jt.addKeyListener(this);
		jt.setText(s);
		jt.setEnabled(edible);
		jt.setDisabledTextColor(Color.BLACK);
		return drug;
	}

	/**
	 * testing allergy.
	 */
	public void allergyTest(JTextField eventField){
		FirableBox tempBox = (FirableBox)eventField.getParent();
		String[] drugSpec = eventField.getText().split(" ");
		if (drugSpec.length>1 && !tempBox.isFired()){
			for (String s: MainWindow.patientAllergy.pull()){
				//testing here, all lower case so case insensitive
				if (s.toLowerCase().equals(drugSpec[0].toLowerCase())){
					tempBox.setFired(true);
					new AlertPopup(s);
				}
			}
		}
		if (drugSpec.length<=1 && tempBox.isFired()){
			tempBox.setFired(false);
		}
	}
	
	/**
	 * override for allergy test
	 */
	@Override
	public void populate(Set<String> s, boolean edible){
		super.populate(s, edible);
		if (edible == true){
			for (JComboBox b : this.boxList){
//				System.out.println(((FirableBox) b).isFired());
				allergyTest((JTextField) b.getEditor().getEditorComponent());
			}
			//if edible give a new line
			enterPress(this.boxList.size()-1,((JTextField) this.boxList.get(this.boxList.size()-1).getEditor().getEditorComponent()).getText().length());			
		}
	}

	/**
	 * test if the drug is allergy agent when keyboard input.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		JTextField jt = (JTextField)e.getSource();
		allergyTest(jt);
//		FirableBox tempBox = (FirableBox)jt.getParent();
//		//		int i = this.boxList.indexOf(tempBox);
//		//		int pos = jt.getCaretPosition();
//		String[] drugSpec = jt.getText().split(" ");
//		if (drugSpec.length>1 && !tempBox.isFired()){
//			for (String s: MainWindow.patientAllergy.pull()){
//				if (s.equals(drugSpec[0])){
//					new AlertPopup(s);
//					tempBox.setFired(true);
//				}
//			}
//		}
//		if (drugSpec.length<=1 && tempBox.isFired()){
//			tempBox.setFired(false);
//		}
	}

	public class SearchBoxModel extends AbstractListModel
	implements ComboBoxModel, KeyListener, ItemListener{
		private Set<String> db = new HashSet<String>();
		private ArrayList<String> data = new ArrayList<String>();
		private String selection;
		private JComboBox cb;
		private ComboBoxEditor cbe;
		private int currPos = 0;

		public SearchBoxModel(JComboBox jcb, Set<String> textList) {

			cb = jcb;
			cbe = jcb.getEditor();
			//here we add the key listener to the text field that the combobox is wrapped around
			cbe.getEditorComponent().addKeyListener(this);

			//set up our "database" of items - in practice you will usuallu have a proper db.
			db.addAll(textList);
			//	        db.add("test 12 mg");
			//	        db.add("test 22 mg");
			//	        db.add("test 32 mg");
			//	        db.add("safwan");
			//	        db.add("safwan 2 mg");
			//	        db.add("safwan 32 mg");
		}

		public void updateModel(String in){
			data.clear();
			//lets find any items which start with the string the user typed, and add it to the popup list
			//here you would usually get your items from a database, or some other storage...
			for(String s:db)
				if(s.startsWith(in))
					data.add(s);

			super.fireContentsChanged(this, 0, data.size());

			//this is a hack to get around redraw problems when changing the list length of the displayed popups
			cb.hidePopup();
			if(data.size() != 0){
				cb.showPopup();
				cb.setSelectedIndex(-1);
			}
		}

		public int getSize(){
			return data.size();
		}

		public Object getElementAt(int index){
			return data.get(index);
		}

		public void setSelectedItem(Object anItem){
			selection = (String) anItem;
		}

		public Object getSelectedItem(){
			return selection;
		}

		public void keyTyped(KeyEvent e){}
		public void keyPressed(KeyEvent e){}

		public void keyReleased(KeyEvent e)
		{
			String str = cbe.getItem().toString();
			JTextField jtf = (JTextField)cbe.getEditorComponent();
			currPos = jtf.getCaretPosition();

			if(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
			{
				if(e.getKeyCode() != KeyEvent.VK_ENTER )
				{
					cbe.setItem(str);
					jtf.setCaretPosition(currPos);
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_ENTER)
				cb.setSelectedIndex(cb.getSelectedIndex());
			else if (cb.getEditor().getItem().toString().length()>4)
			{
				updateModel(cb.getEditor().getItem().toString());
				cbe.setItem(str);
				jtf.setCaretPosition(currPos);
			}else{
				updateModel("!@dummy item");
				cbe.setItem(str);
				jtf.setCaretPosition(currPos);
			}
		}

		public void itemStateChanged(ItemEvent e)
		{
			cbe.setItem(e.getItem().toString());
			cb.setSelectedItem(e.getItem());
		}

	}

	class ComboBoxRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer  
	{  
		public ComboBoxRenderer()  
		{  
			super();  
			setOpaque(true);  
		}  
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus)  
		{  
			setText(value.toString());  
			if(isSelected) {
				setBackground(Color.decode("#335b8e")); 
				setForeground(Color.WHITE);
			}
			else {setBackground(Color.WHITE); setForeground(Color.BLACK);}
			return this;  
		}  
	}  //inner class

	public class FirableBox extends JComboBox<JTextField>{

		private static final long serialVersionUID = -5970625848197390449L;

		private boolean fired = false;

		public boolean isFired(){
			return this.fired;
		}

		public void setFired(boolean fire){
			this.fired=fire;
		}
	}//inner class
}
